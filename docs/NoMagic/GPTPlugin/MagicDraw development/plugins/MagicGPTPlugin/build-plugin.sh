#!/usr/bin/env bash
set -euo pipefail

PLUGIN_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$PLUGIN_DIR/src"
BUILD_DIR="$PLUGIN_DIR/build/classes"
DIST_DIR="$PLUGIN_DIR/dist"
OUTPUT_DIR="$DIST_DIR/MagicGPTPlugin"
LIB_DIR="$OUTPUT_DIR/lib"
JAR_NAME="MagicGPTPlugin.jar"
ZIP_NAME="MagicGPTPlugin.zip"

if [[ -z "${MAGICDRAW_HOME:-}" ]]; then
  echo "MAGICDRAW_HOME 환경 변수가 필요합니다." >&2
  exit 1
fi

if [[ ! -d "$MAGICDRAW_HOME" ]]; then
  echo "MAGICDRAW_HOME 경로를 찾을 수 없습니다: $MAGICDRAW_HOME" >&2
  exit 1
fi

mkdir -p "$BUILD_DIR" "$LIB_DIR" "$DIST_DIR"

CLASSPATH=$(find "$MAGICDRAW_HOME" -type f -name "*.jar" | tr '\n' ':')

find "$SRC_DIR" -type f -name "*.java" > "$BUILD_DIR/sources.txt"

javac -encoding UTF-8 -cp "$CLASSPATH" -d "$BUILD_DIR" @"$BUILD_DIR/sources.txt"

jar cf "$LIB_DIR/$JAR_NAME" -C "$BUILD_DIR" .

cp "$PLUGIN_DIR/plugin.xml" "$OUTPUT_DIR/plugin.xml"

(
  cd "$DIST_DIR"
  rm -f "$ZIP_NAME"
  zip -r "$ZIP_NAME" "MagicGPTPlugin" >/dev/null
)

echo "빌드 완료: $DIST_DIR/$ZIP_NAME"
