$ErrorActionPreference = "Stop"

$securePassword = Read-Host "Nhập mật khẩu MySQL" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    $env:DB_USERNAME = if ($env:DB_USERNAME) { $env:DB_USERNAME } else { "root" }
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

    $jarPath = Join-Path $PSScriptRoot "target\springboot-product-single-image-0.0.1-SNAPSHOT.jar"
    if (-not (Test-Path -LiteralPath $jarPath)) {
        throw "Chưa có file JAR. Hãy chạy 'mvn clean package' trước."
    }

    java -jar $jarPath
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
