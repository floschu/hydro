# hydro

simple hydration reminder app

![logo](.media/icon.png)

## how to create a release

### local
* Set up signing
    * put `keystore.jks` into `app` folder
    * add `signingKeyAlias`, `signingKeyPassword`, `signingStorePassword` to `local.properties`
* Execute `./gradlew :app:assembleRelease`

### github
* Set up signing by adding the following secrets to github:
    * `KEYSTORE_ENCRYPTED`: base64 encrypted `keystore.jks` file (`base64 -i [Jks FilePath] -o [EncodeFilePath].txt`)
    * `KEYSTORE_KEY_ALIAS`: key alias
    * `KEYSTORE_KEY_PASSWORD`: key password
    * `KEYSTORE_STORE_PASSWORD`: store password
* Push a new commit with version tag (e.g. `v2.0.0-b36`) to `develop` branch