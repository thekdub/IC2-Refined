#!/usr/bin/env bash

./gradlew jar

# Remove current jar (if exists)
rm -f ./server/mods/ic2refined-*.jar

# Move the newly built jar into server mods folder
cp -v ./build/libs/ic2refined-*.jar ./server/mods/

# Start the server
cd ./server && sh ./launch.sh