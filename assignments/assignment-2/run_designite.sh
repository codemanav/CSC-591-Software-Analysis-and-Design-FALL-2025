#!/bin/bash

# DesigniteJava Analysis Script
# This script runs DesigniteJava on the transactions-service

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DESIGNITE_JAR="$SCRIPT_DIR/designitejava/DesigniteJava.jar"
SOURCE_DIR="$SCRIPT_DIR/transactions-service/src/main/java"
OUTPUT_DIR="$SCRIPT_DIR/designitejava/output"

# Check if DesigniteJava.jar exists
if [ ! -f "$DESIGNITE_JAR" ]; then
    echo "❌ Error: DesigniteJava.jar not found!"
    echo "Please download DesigniteJava.jar and place it in: $SCRIPT_DIR/designitejava/"
    echo ""
    echo "Download from: https://www.designite-tools.com/designitejava"
    exit 1
fi

# Check Java version
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed or not in PATH"
    exit 1
fi

echo "🔍 Running DesigniteJava Analysis..."
echo "Source: $SOURCE_DIR"
echo "Output: $OUTPUT_DIR"
echo ""

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Run DesigniteJava
java -jar "$DESIGNITE_JAR" -i "$SOURCE_DIR" -o "$OUTPUT_DIR"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Analysis complete!"
    echo "📊 Results saved to: $OUTPUT_DIR"
    echo ""
    echo "Key files to check:"
    echo "  - CodeSmells.csv"
    echo "  - DesignSmells.csv"
    echo "  - ArchitecturalSmells.csv"
    echo ""
    echo "To view results:"
    echo "  cd $OUTPUT_DIR"
    echo "  open ."
else
    echo "❌ Analysis failed. Check the error messages above."
    exit 1
fi
