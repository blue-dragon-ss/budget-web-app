# 使用例: .\scripts\next-version.ps1 -Current v0.2.0 -Part patch
param(
  [Parameter(Mandatory=$true)][string]$Current,
  [ValidateSet("major","minor","patch")][string]$Part = "patch"
)

if ($Current -notmatch "^v(\d+)\.(\d+)\.(\d+)$") {
  Write-Error "タグ形式が不正です: vMAJOR.MINOR.PATCH"
  exit 1
}

$major = [int]$Matches[1]
$minor = [int]$Matches[2]
$patch = [int]$Matches[3]

switch ($Part) {
  "major" { $major++; $minor=0; $patch=0 }
  "minor" { $minor++; $patch=0 }
  "patch" { $patch++ }
}

$new = "v$major.$minor.$patch"
Write-Output $new
