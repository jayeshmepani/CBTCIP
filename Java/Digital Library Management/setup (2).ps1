# Define the base directory for the project
$baseDir = "E:\project\CipherByte Tech\java\Digital Library Management"

# Define the folders to be created
$folders = @(
    "\src\com\library\admin",
    "\src\com\library\database",
    "\src\com\library\models",
    "\src\com\library\user",
    "\src\com\library\utils"
)

# Define the files to be created
$files = @(
    "\src\com\library\admin\AdminDashboard.java",
    "\src\com\library\database\DBConnection.java",
    "\src\com\library\database\DBQueries.java",
    "\src\com\library\models\Book.java",
    "\src\com\library\models\Member.java",
    "\src\com\library\models\Transaction.java",
    "\src\com\library\user\UserDashboard.java",
    "\src\com\library\utils\EmailService.java",
    "\src\com\library\LibrarySystem.java",
    "\librarydb.sql"
)

# Create the directories
foreach ($folder in $folders) {
    $path = Join-Path $baseDir $folder
    if (-not (Test-Path $path)) {
        New-Item -Path $path -ItemType Directory
    }
}

# Create the files
foreach ($file in $files) {
    $path = Join-Path $baseDir $file
    if (-not (Test-Path $path)) {
        New-Item -Path $path -ItemType File
    }
}

Write-Host "Project structure created successfully at $baseDir"
