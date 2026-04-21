$ErrorActionPreference = 'Stop'

try {
    Write-Host "Registering Alice..."
    $aliceJSON = '{"name": "Alice", "email": "alice@test.com", "password": "pass"}'
    $aliceResponse = Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:8085/api/auth/register" -Method Post -ContentType "application/json" -Body $aliceJSON
    Write-Host "Response: " $aliceResponse.Content
    Write-Host ""

    Write-Host "Registering Bob..."
    $bobJSON = '{"name": "Bob", "email": "bob@test.com", "password": "pass"}'
    $bobResponse = Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:8085/api/auth/register" -Method Post -ContentType "application/json" -Body $bobJSON
    Write-Host "Response: " $bobResponse.Content
    Write-Host ""
} catch {
    Write-Host "Exception: $_"
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Host "Server Response Body: " $reader.ReadToEnd()
    }
}

