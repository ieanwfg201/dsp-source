Install s3fs and add new bucket. 
- Get access keys for your account from AWS -> Account -> Security Credentials -> Access Credentials -> Access Keys -> Your Access Keys
- If access key doesn’t exist, create new and download. The key file will be in the format
    - AWSAccessKeyId=<key id>
    - AWSSecretKey=<secret key>
- Create a new file with the following format
    - <key id>:<secret key> where the key id and secret key are from the previous file
- Now run installs3fs.sh from ysoserious/utils/amazon_s3_upload
    - You need to provide 2 arguments to the script, the path to the file created in last step and your s3 bucket name.
- The s3 bucket is now mounted as /mnt/<bucket name> and you can use it as a normal drive. Copying and deleting files from it.

Add new s3 bucket
- Run addnews3bucket.sh from ysoserious/utils/amazon_s3_upload
    - You need to provide 1 argument to the script, your s3 bucket name.
- The s3 bucket is now mounted as /mnt/<bucket name> and you can use it as a normal drive. Copying and deleting files from it.
