$ErrorActionPreference = "Stop"

$PluginDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$SrcDir = Join-Path $PluginDir "src"
$BuildDir = Join-Path $PluginDir "build\\classes"
$DistDir = Join-Path $PluginDir "dist"
$OutputDir = Join-Path $DistDir "MagicGPTPlugin"
$LibDir = Join-Path $OutputDir "lib"
$PluginLibDir = Join-Path $PluginDir "lib"
$JarName = "MagicGPTPlugin.jar"
$ZipName = "MagicGPTPlugin.zip"

if (-not $env:MAGICDRAW_HOME) {
  throw "MAGICDRAW_HOME is not set."
}

if (-not (Test-Path $env:MAGICDRAW_HOME)) {
  throw "MAGICDRAW_HOME path does not exist: $env:MAGICDRAW_HOME"
}

New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null
New-Item -ItemType Directory -Force -Path $LibDir | Out-Null
New-Item -ItemType Directory -Force -Path $DistDir | Out-Null
New-Item -ItemType Directory -Force -Path $PluginLibDir | Out-Null

$classpath = Join-Path $env:MAGICDRAW_HOME "lib\\classpath.jar"
$libJars = @()
if (Test-Path $PluginLibDir) {
  $libJars = Get-ChildItem -Path $PluginLibDir -Filter *.jar | Select-Object -ExpandProperty FullName
  if ($libJars.Count -gt 0) {
    $classpath = $classpath + ";" + ($libJars -join ";")
  }
}

$sourceFiles = Get-ChildItem -Path $SrcDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName

javac -encoding UTF-8 -cp $classpath -d $BuildDir $sourceFiles
if ($LASTEXITCODE -ne 0) {
  throw "javac failed with exit code $LASTEXITCODE"
}

jar cf (Join-Path $LibDir $JarName) -C $BuildDir .

Copy-Item -Path (Join-Path $PluginDir "plugin.xml") -Destination (Join-Path $OutputDir "plugin.xml") -Force
if ($libJars.Count -gt 0) {
  Copy-Item -Path $libJars -Destination $LibDir -Force
}

$zipPath = Join-Path $DistDir $ZipName
if (Test-Path $zipPath) {
  Remove-Item $zipPath -Force
}

Compress-Archive -Path $OutputDir -DestinationPath $zipPath

Write-Host "Build complete: $zipPath"
