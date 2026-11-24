#!/usr/bin/env python3
"""Pick the unit test groups CI runs for a change.

A pull request runs the test groups its changes can affect; a push to main runs every group. Each
changed file is classified by the first rule it matches:

- A test class runs the groups whose --tests filters include it. A test file no group runs, such as
  a helper, can affect anything, so it runs every group.
- Any other file under yagsl or vendordep runs every group: the parser, the vendordeps and the build
  files are used by every test.
- This workflow, this script and test_summary.py run every group.
- Files elsewhere, such as the docs and the examples (which have their own workflow), run nothing.

Usage: select_test_groups.py [--all] < changed-files
Reads the changed files, one per line, unless --all. Prints a `java=<matrix JSON>` line for
$GITHUB_OUTPUT, and why each file runs its groups to stderr. Fails if a test class under
vendordep/src/test/java is in no group, since Gradle's --tests filters would never run it.
"""

import fnmatch
import json
import re
import sys
from pathlib import Path

# Java unit test groups. Each runs on its own runner. Gradle's --tests cannot exclude classes, so
# every test class has to match a group: add new test classes to one.
JAVA_GROUPS = [
    # Default config generator gains on simulated swerve drives, one motor family per JVM. The Novas
    # are stepped a robot loop at a time, but take the longest.
    {"group": "sim-gains-nova", "test-filter": '--tests "swervelib.commands2.SimGainsNova*"'},
    # TalonFX and TalonFXS drives, which Phoenix simulates in real time.
    {"group": "sim-gains-talon", "test-filter": '--tests "swervelib.commands2.SimGainsTalonFX*"'},
    {"group": "sim-gains-spark", "test-filter": '--tests "swervelib.commands2.SimGainsSpark*"'},
    # Every _attached absolute encoder type on each angle motor controller, in simulation.
    {"group": "attached-encoders", "test-filter": '--tests "swervelib.commands2.AttachedEncoder*"'},
    # Whole swerve drives built from JSON through the vendor reflection path: Novas, Falcons, a NavX3
    # on CAN and over USB.
    {"group": "parser-devices",
     "test-filter": '--tests "swervelib.commands2.SwerveParserNovaTest"'
                    ' --tests "swervelib.commands2.SwerveParserFalconTest"'
                    ' --tests "swervelib.commands2.SwerveParserNavxTest"'
                    ' --tests "swervelib.commands2.SwerveParserNavxUsbTest"'},
    # The JSON parser, schemas, CAN buses and reflection, without simulated drives.
    {"group": "parser", "test-filter": '--tests "swervelib.core.*"'},
]

JAVA_TESTS = "vendordep/src/test/java/"

# Changed files that run every group.
CI = [".github/workflows/ci.yml", ".github/scripts/select_test_groups.py",
      ".github/scripts/test_summary.py"]

# Changed files that affect no unit test.
NO_TESTS = ["*.md", "examples/*", "vendordep/README.md"]


def names():
    return {group["group"] for group in JAVA_GROUPS}


def groups_of(class_name):
    """The groups whose --tests filters include a test class."""
    return {group["group"] for group in JAVA_GROUPS
            for test in re.findall(r'--tests "([^"]+)"', group["test-filter"])
            if fnmatch.fnmatch(class_name, test)}


def class_name(path):
    return path[len(JAVA_TESTS):].removesuffix(".java").replace("/", ".")


def select(path):
    """The groups a changed file affects."""
    if path in CI:
        return names()
    if any(fnmatch.fnmatch(path, pattern) for pattern in NO_TESTS):
        return set()
    if path.startswith(JAVA_TESTS) and path.endswith(".java"):
        return groups_of(class_name(path)) or names()
    if path.startswith("yagsl/") or path.startswith("vendordep/"):
        return names()
    return set()


def ungrouped_tests():
    """Test classes no group runs. Abstract classes don't run on their own, so they are left out."""
    tests = Path(JAVA_TESTS)
    return sorted(class_name(str(path)) for path in tests.rglob("*Test.java")
                  if not re.search(r"\babstract\s+class\b", path.read_text(encoding="utf-8"))
                  and not groups_of(class_name(str(path))))


def main(args):
    ungrouped = ungrouped_tests()
    if ungrouped:
        sys.exit("Test classes in no group, which CI would never run: " + ", ".join(ungrouped)
                 + ". Add them to a group in .github/scripts/select_test_groups.py.")
    run_all = "--all" in args
    selected = names() if run_all else set()
    if not run_all:
        for path in (line.strip() for line in sys.stdin):
            if not path:
                continue
            groups = select(path)
            if groups:
                print(f"{path}: {sorted(groups)}", file=sys.stderr)
            selected |= groups
    print("java=" + json.dumps([group for group in JAVA_GROUPS if group["group"] in selected]))


if __name__ == "__main__":
    main(sys.argv[1:])
