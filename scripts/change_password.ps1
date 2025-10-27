param(
  [string]$Token,
  [string]$CsrfToken,
  [string]$CurrentPassword,
  [string]$NewPassword,
  [string]$ConfirmPassword,
  [string]$BaseUrl = 'http://localhost:8080'
)

if (-not $Token -or -not $CsrfToken) {
  Write-Error 'Token/CsrfToken required'
  exit 1
}

$headers = @{ 'Authorization' = "Bearer $Token"; 'X-CSRF-Token' = $CsrfToken; 'Content-Type' = 'application/json' }
$body = @{ currentPassword = $CurrentPassword; newPassword = $NewPassword; confirmPassword = $ConfirmPassword } | ConvertTo-Json -Compress

try {
  $resp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/users/change-password" -Headers $headers -Body $body
  $resp | ConvertTo-Json -Depth 5
} catch {
  $_.Exception.Message | Out-Host
  if ($_.ErrorDetails) { $_.ErrorDetails.Message | Out-Host }
  exit 1
}