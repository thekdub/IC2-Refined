#!/usr/bin/env bash

# Create temp server folder
mkdir ./server && cd ./server

# Unzip all the tekkit server contents
unzip -o ../lib/Tekkit_Server_3.1.2.zip

# Remove IC2 mod, which we will replace with our own when `build.sh` runs
rm mods/industrialcraft2-1.97-mcpc1.2.5-r7.zip