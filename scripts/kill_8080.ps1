param(
  [int]$Port = 8080
)

Write-Host "=== netstat for :$Port ==="
$lines = netstat -ano | Select-String (":" + $Port)
$lines | ForEach-Object { $_.ToString() } | Out-Host

$pids = @()
foreach ($l in $lines) {
  $parts = ($l.ToString() -split '\s+')
  if ($parts.Length -gt 0) {
    $pid = $parts[$parts.Length - 1]
    if ($pid -match '^[0-9]+$') { $pids += [int]$pid }
  }
}
$pids = $pids | Sort-Object -Unique
Write-Host ("Found PIDs: " + (($pids | ForEach-Object { $_.ToString() }) -join ", "))

if ($pids.Count -gt 0) {
  foreach ($pid in $pids) {
    try {
      Stop-Process -Id $pid -Force -ErrorAction Stop
      Write-Host ("Stopped PID " + $pid)
    } catch {
      Write-Warning ("Failed to stop PID " + $pid + ": " + $_.Exception.Message)
    }
  }
} else {
  Write-Host "No processes found using port $Port."
}
