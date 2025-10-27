param(
  [string]$BaseUrl = 'http://localhost:8081',
  [string]$Username = 'admin',
  [string]$Password = '666666'
)

$loginBody = @{ username = $Username; password = $Password } | ConvertTo-Json -Compress
try {
  $loginResp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/login" -ContentType 'application/json' -Body $loginBody -ErrorAction Stop
} catch {
  $_.Exception.Message | Out-Host
  if ($_.ErrorDetails) { $_.ErrorDetails.Message | Out-Host }
  exit 1
}

$token = $loginResp.data.token
if (-not $token) { Write-Error 'Login failed: token missing'; exit 1 }

$headers = @{ 'Authorization' = "Bearer $token" }
try {
  $users = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/users?page=1&size=10" -Headers $headers -ErrorAction Stop
  $users | ConvertTo-Json -Depth 6
} catch {
  $_.Exception.Message | Out-Host
  if ($_.ErrorDetails) { $_.ErrorDetails.Message | Out-Host }
  exit 1
}
