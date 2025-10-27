param(
  [string]$Username = 'admin',
  [string]$Password = '666666',
  [string]$BaseUrl = 'http://localhost:8080'
)

$body = @{ username = $Username; password = $Password } | ConvertTo-Json -Compress
try {
  $resp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/login" -ContentType 'application/json' -Body $body
  $resp | ConvertTo-Json -Depth 5
} catch {
  $_.Exception.Message | Out-Host
  if ($_.ErrorDetails) { $_.ErrorDetails.Message | Out-Host }
  exit 1
}