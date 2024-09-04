# Create the lib directory
mkdir "E:\project\CipherByte Tech\java\Digital Library Management\lib"

# Move the JAR files to the lib directory
Move-Item "E:\project\CipherByte Tech\java\Digital Library Management\mysql-connector-j-9.0.0\mysql-connector-j-9.0.0.jar" "E:\project\CipherByte Tech\java\Digital Library Management\lib\"
Move-Item "E:\project\CipherByte Tech\java\Digital Library Management\javax.mail.jar" "E:\project\CipherByte Tech\java\Digital Library Management\lib\"

# Optional: Clean up the mysql-connector-j-9.0.0 folder if it's not needed anymore
Remove-Item "E:\project\CipherByte Tech\java\Digital Library Management\mysql-connector-j-9.0.0" -Recurse
