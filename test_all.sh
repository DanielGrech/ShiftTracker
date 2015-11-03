#!/bin/bash

DIVIDER="\n\n==========\n\n"

printf "$DIVIDER Unit testing models...$DIVIDER"

./gradlew :model:test :model:jacocoTestReport

printf "$DIVIDER Unit testing data...$DIVIDER"

./gradlew :data:testPaidJacocoEnabledUnitTest :data:testPaidJacocoEnabledUnitTestCoverage

printf "$DIVIDER Unit testing app...$DIVIDER"

./gradlew :app:testPaidJacocoEnabledUnitTest :app:testPaidJacocoEnabledUnitTestCoverage