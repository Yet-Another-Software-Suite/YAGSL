"""Check every vendordep JSON in the repository is the latest version.

The latest version of a vendordep is the newest of what its jsonUrl publishes and what WPILib's
vendor JSON repository (the list VS Code installs from) has for its WPILib year, matched by uuid.
Either can lag: some jsonUrls are missing for alpha years, and the WPILib list is updated after a
vendor releases. Prints each vendordep's version against the latest, and exits 1 if any is behind
or its latest version can't be found. WPILib's own command libraries, which have neither, are
skipped.
"""

import json
import pathlib
import re
import sys
import time
import urllib.request

ROOT = pathlib.Path(__file__).resolve().parents[2]
VENDOR_REPO = "https://raw.githubusercontent.com/wpilibsuite/vendor-json-repo/main"
VENDOR_REPO_API = "https://api.github.com/repos/wpilibsuite/vendor-json-repo/contents"
# WPILib's command libraries come with WPILib, not from a vendor.
WPILIB_LIBRARIES = {"CommandsV2.json", "CommandsV3.json"}


def fetch(url):
    """The JSON at url, retried a few times for flaky networks."""
    last = None
    for attempt in range(3):
        try:
            request = urllib.request.Request(url, headers={"User-Agent": "YAGSL-CI"})
            with urllib.request.urlopen(request, timeout=30) as response:
                return json.load(response)
        except Exception as error:  # noqa: BLE001 - retried, then reported
            last = error
            time.sleep(2 * (attempt + 1))
    raise last


def version_key(version):
    """Sort key for vendordep versions such as 2027.0.0-alpha-7p1 or 26.70.0-alpha-2."""
    return [(0, int(token)) if token.isdigit() else (1, token)
            for token in re.findall(r"\d+|[a-zA-Z]+", version or "")]


def wpilib_versions(year, cache):
    """uuid to versions of every vendordep in WPILib's vendor JSON repository for year."""
    if year not in cache:
        versions = {}
        try:
            for entry in fetch(f"{VENDOR_REPO_API}/{year}"):
                if entry["name"].endswith(".json"):
                    vendordep = fetch(f"{VENDOR_REPO}/{year}/{entry['name']}")
                    versions.setdefault(vendordep.get("uuid"), []).append(vendordep.get("version"))
        except Exception as error:  # noqa: BLE001
            print(f"warning: could not read WPILib's vendor JSON repository for {year}: {error}")
        cache[year] = versions
    return cache[year]


def main():
    files = sorted(
        path for path in ROOT.glob("**/vendordeps/*.json")
        if path.name not in WPILIB_LIBRARIES
        and not any(part in ("build", "node_modules", ".gradle") for part in path.parts))
    url_cache = {}
    year_cache = {}
    rows = []
    failures = []
    for path in files:
        vendordep = json.loads(path.read_text(encoding="utf-8"))
        file = str(path.relative_to(ROOT)).replace("\\", "/")
        name = vendordep.get("name", path.name)
        version = vendordep.get("version")
        candidates = list(wpilib_versions(vendordep.get("wpilibYear"), year_cache).get(vendordep.get("uuid"), []))
        url = vendordep.get("jsonUrl")
        if url:
            if url not in url_cache:
                try:
                    url_cache[url] = fetch(url).get("version")
                except Exception as error:  # noqa: BLE001
                    url_cache[url] = None
                    print(f"warning: could not read {url}: {error}")
            if url_cache[url]:
                candidates.append(url_cache[url])
        latest = max(candidates, key=version_key) if candidates else None
        if latest is None:
            status = "UNKNOWN"
            failures.append(f"{file}: no latest version of {name} could be found")
        elif version_key(version) < version_key(latest):
            status = "OUT OF DATE"
            failures.append(f"{file}: {name} is {version}, the latest is {latest}")
        else:
            status = "ok"
        rows.append((file, name, version, latest or "?", status))

    width = max(len(row[0]) for row in rows)
    for file, name, version, latest, status in rows:
        print(f"{file:<{width}}  {name:<45}  {version:<20}  {latest:<20}  {status}")
    if failures:
        print("\nVendordeps that are not the latest version:")
        for failure in failures:
            print(f"  {failure}")
        sys.exit(1)
    print("\nEvery vendordep is the latest version.")


if __name__ == "__main__":
    main()
