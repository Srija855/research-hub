#!/bin/bash
# TextHack Build Script
set -e
echo "Building TextHack Research Hub..."
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")
echo "Build successful! Binaries compiled into bin/"
