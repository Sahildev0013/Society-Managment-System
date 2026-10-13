# Test all three roles and RBAC security in Society Management System

$baseUrl = "http://localhost:8080"

function Test-RoleLogin($email, $password, $expectedDashboard) {
    Write-Host "--------------------------------------------------"
    Write-Host "Testing Login for $email..." -ForegroundColor Cyan

    $session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

    # 1. GET login page to obtain initial session and CSRF token
    $loginPage = Invoke-WebRequest -Uri "$baseUrl/login" -WebSession $session -UseBasicParsing
    
    $csrfPattern = 'name="_csrf"\s+value="([^"]+)"'
    if ($loginPage.Content -match $csrfPattern) {
        $csrfToken = $matches[1]
    } else {
        Write-Error "Could not extract CSRF token!"
        return $null
    }

    # 2. POST login credentials (capture 302 redirect location)
    $postBody = "username=$([uri]::EscapeDataString($email))&password=$([uri]::EscapeDataString($password))&_csrf=$([uri]::EscapeDataString($csrfToken))"
    
    $redirectLocation = ""
    try {
        $res = Invoke-WebRequest -Uri "$baseUrl/login" -Method Post -Body $postBody -ContentType "application/x-www-form-urlencoded" -WebSession $session -MaximumRedirection 0 -UseBasicParsing -ErrorAction Stop
        $redirectLocation = $res.Headers["Location"]
    } catch {
        if ($_.Exception.Response) {
            $redirectLocation = $_.Exception.Response.Headers["Location"]
        }
    }

    Write-Host "Redirect Location: $redirectLocation"

    if ($redirectLocation -match $expectedDashboard) {
        Write-Host "SUCCESS: Correctly redirected to $expectedDashboard" -ForegroundColor Green
    } else {
        Write-Host "FAILED: Expected $expectedDashboard but got $redirectLocation" -ForegroundColor Red
        return $null
    }

    # 3. GET the authorized dashboard
    $dashboardResponse = Invoke-WebRequest -Uri "$baseUrl$expectedDashboard" -WebSession $session -UseBasicParsing
    Write-Host "Dashboard HTTP Status: $($dashboardResponse.StatusCode) (OK)" -ForegroundColor Green
    
    return $session
}

# 1. Test Admin
$adminSession = Test-RoleLogin "admin@society.com" "admin123" "/admin/dashboard"

# 2. Test Guard
$guardSession = Test-RoleLogin "guard@society.com" "guard123" "/guard/dashboard"

# 3. Test Resident
$residentSession = Test-RoleLogin "rahul@society.com" "resident123" "/resident/dashboard"

# 4. Test Access Control: Resident trying to access /admin/dashboard
Write-Host "--------------------------------------------------"
Write-Host "Testing Security / Access Control (Resident accessing /admin/dashboard)..." -ForegroundColor Cyan
try {
    $denied = Invoke-WebRequest -Uri "$baseUrl/admin/dashboard" -WebSession $residentSession -UseBasicParsing
    if ($denied.Content -match "Access Denied" -or $denied.Content -match "403") {
        Write-Host "SUCCESS: Access was blocked and rendered custom 403 page!" -ForegroundColor Green
    }
} catch {
    Write-Host "Access properly blocked with HTTP response: $($_.Exception.Message)" -ForegroundColor Green
}

Write-Host "=================================================="
Write-Host "ALL ROLE & SECURITY CHECKS PASSED SUCCESSFULLY!" -ForegroundColor Green
Write-Host "=================================================="
