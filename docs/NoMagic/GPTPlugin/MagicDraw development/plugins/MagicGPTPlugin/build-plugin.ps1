$ErrorActionPreference = "Stop"

$PluginDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$SrcDir = Join-Path $PluginDir "src"
$BuildDir = Join-Path $PluginDir "build\classes"
$DistDir = Join-Path $PluginDir "dist"
$OutputDir = Join-Path $DistDir "MagicGPTPlugin"
$LibDir = Join-Path $OutputDir "lib"
$JarName = "MagicGPTPlugin.jar"
$ZipName = "MagicGPTPlugin.zip"

if (-not $env:MAGICDRAW_HOME) {
  throw "MAGICDRAW_HOME 환경 변수가 필요합니다."
}

if (-not (Test-Path $env:MAGICDRAW_HOME)) {
  throw "MAGICDRAW_HOME 경로를 찾을 수 없습니다: $env:MAGICDRAW_HOME"
}

New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null
New-Item -ItemType Directory -Force -Path $LibDir | Out-Null
New-Item -ItemType Directory -Force -Path $DistDir | Out-Null

$jarFiles = Get-ChildItem -Path $env:MAGICDRAW_HOME -Recurse -Filter *.jar | Select-Object -ExpandProperty FullName
$classpath = ($jarFiles -join ";")

$sourceFiles = Get-ChildItem -Path $SrcDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
$sourceList = Join-Path $BuildDir "sources.txt"
$sourceFiles | Set-Content -Path $sourceList

javac -encoding UTF-8 -cp $classpath -d $BuildDir @"$sourceList"

jar cf (Join-Path $LibDir $JarName) -C $BuildDir .

Copy-Item -Path (Join-Path $PluginDir "plugin.xml") -Destination (Join-Path $OutputDir "plugin.xml") -Force

$zipPath = Join-Path $DistDir $ZipName
if (Test-Path $zipPath) {
  Remove-Item $zipPath -Force
}

Compress-Archive -Path $OutputDir -DestinationPath $zipPath

Write-Host "빌드 완료: $zipPath"
