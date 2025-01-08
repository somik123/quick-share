[![Docker Image Build](https://github.com/somik123/quick-share/actions/workflows/main.yaml/badge.svg)](https://github.com/somik123/quick-share/actions/workflows/main.yaml)

# quick-share
A online chat like interface for quickly sharing text/links/files with other devices


### Installation
Copy/download the `docker-compose.yml` file and run it. You do not require any of the other source files unless you want to build the image yourself.
```
curl -o docker-compose.yml https://raw.githubusercontent.com/somik123/quick-share/main/docker-compose.yml
```

Edit the file with your favorit editor to set all the environment variables in the docker-compose file, including the admin username/password:

| Env vars | Details |
|----------|---------|
| `QUICKSHARE_DB_NAME` | Database name. Database will be saved to ./db folder. Tables will be autocreated on boot (if not exist) |
| `QUICKSHARE_DB_USER` | Database usernme |
| `QUICKSHARE_DB_PASS` | Database password |
| `FILESHARE_SITE_FULL_URL` | Homepage for https://github.com/somik123/link-shortener-file-share |
| `IMAGESHARE_SITE_FULL_URL` | Homepage url for https://github.com/somik123/mini-image-host |

Once done, save the file and run the following command from the same folder as your `docker-compose.yml` file.
```
docker compose up -d
```

The website will be on port `6980` on the server you run it on. Use nginx proxy to secure it with https.

<br>

### DB Manager

Access the H2 database console on `SITE_FULL_URL/h2-QUICKSHARE_DB_NAME` where `SITE_FULL_URL` is your website's home address and `QUICKSHARE_DB_NAME` is what you defined in your `docker-compose.yml` file. 

So if your website url is `http://example.com/` and you named your db `XYgPZX7WCiW` your H2 console url is `http://example.com/h2-XYgPZX7WCiW`

At the login page:

| Title | Value |
|-------|-------|
|Driver Class:| org.h2.Driver |
|JDBC URL:| jdbc:h2:file:./db/`QUICKSHARE_DB_NAME` |
|User Name:| `QUICKSHARE_DB_USER` |
|Password:| `QUICKSHARE_DB_PASS` |

> Note: Replace `QUICKSHARE_DB_NAME`, `QUICKSHARE_DB_USER` and `QUICKSHARE_DB_PASS` with what you defined in your `docker-compose.yml` file.

<br>

<details>
<summary><b>MySQL DB Manager</b> (Depricated)</summary>
PhpMyAdmin database manager is disabled by default in the `docker-compose.yml` file. It provides a simple way to troubleshoot or edit your bucket database but use it at your own risk.

To use it, copy paste the following code block at the bottom of your `docker-compose.yml` file and run the `docker compose up -d` command.

```
  db_manager:
    image: phpmyadmin
    container_name: phpmyadmin
    restart: unless-stopped
    environment:
      TZ: Asia/Singapore
      PMA_HOST: db
    ports:
      - 6988:80
    depends_on:
      - db
    links:
      - db
```
It is available on port `6988` once it is up. 

<br>
</details>

### Screenshots

<img src="https://raw.githubusercontent.com/somik123/quick-share/main/screenshots/1.png">

<img src="https://raw.githubusercontent.com/somik123/quick-share/main/screenshots/2.png">

<br>

### To-do list
- [x] Add embeded database support (using h2 db).
- [x] Add env variables for db settings.
- [x] Ability to upload files 
- [x] Able to delete messages.
- [x] Add file upload & share feature using https://github.com/somik123/link-shortener-file-share
- [x] Add image upload & share feature using https://github.com/somik123/mini-image-host
- [x] Expire/delete the messages after predefined time.
- [x] Add option to encrypt each message with a password.
- [x] Add input validations for message box name & message expiry.
- [x] Add a favicon icon.
- [x] Allow attaching images.
- [x] Add max text length for messages.
- [x] Add message text length counter for messages.
- [x] Set message boxes max height.
- [x] Add a footer.
- [x] Add option to share message box without typing in the message box name.
- [x] Add ability to open message in larger view.
- [x] Add gravater for users.
- [x] Make the UI look more like a forum.
- [x] Change the date/times to be relative.
- [x] Add username and message box name filters to prevent misuse.
- [x] Save the username in a cookie to remember it.
- [ ] Add admin login to manage message boxes & messages.
- [ ] Add flag to enable only admin to create message boxes.
- [ ] Add user authentications.
- [ ] (more)
- [ ] Test for vulnerabilities.
