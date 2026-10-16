#!/bin/bash
# TextHack Execution Script
set -e
if [ ! -d "bin" ] || [ ! -f "bin/texthack/TextHackApp.class" ]; then
    ./build.sh
fi
java -cp bin texthack.TextHackApp "$@"
