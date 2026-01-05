# PowerShell script to push to GitHub
# Run this after creating your GitHub repository

Write-Host "=== GitHub Push Script ===" -ForegroundColor Green
Write-Host ""

# Get GitHub username
$username = Read-Host "Enter your GitHub username"
$repoName = Read-Host "Enter repository name (default: realtor-success-iq)" 
if ([string]::IsNullOrWhiteSpace($repoName)) {
    $repoName = "realtor-success-iq"
}

Write-Host ""
Write-Host "Setting up remote..." -ForegroundColor Yellow

# Add remote
git remote add origin "https://github.com/$username/$repoName.git"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Remote added successfully!" -ForegroundColor Green
} else {
    Write-Host "Remote might already exist. Checking..." -ForegroundColor Yellow
    git remote set-url origin "https://github.com/$username/$repoName.git"
}

Write-Host ""
Write-Host "Pushing to GitHub..." -ForegroundColor Yellow
Write-Host "You may be prompted for credentials." -ForegroundColor Cyan
Write-Host "Use your GitHub username and a Personal Access Token as password." -ForegroundColor Cyan
Write-Host ""

# Push to GitHub
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=== SUCCESS! ===" -ForegroundColor Green
    Write-Host "Your code is now on GitHub!" -ForegroundColor Green
    Write-Host "Visit: https://github.com/$username/$repoName" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Go to Actions tab to see auto-build" -ForegroundColor White
    Write-Host "2. Download APK from Actions artifacts" -ForegroundColor White
    Write-Host "3. Install on your phone!" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "=== Push Failed ===" -ForegroundColor Red
    Write-Host "Common issues:" -ForegroundColor Yellow
    Write-Host "1. Repository doesn't exist - create it on GitHub first" -ForegroundColor White
    Write-Host "2. Authentication failed - use Personal Access Token" -ForegroundColor White
    Write-Host "3. Wrong username/repo name - check and try again" -ForegroundColor White
    Write-Host ""
    Write-Host "See GITHUB_NEXT_STEPS.md for detailed instructions" -ForegroundColor Cyan
}

