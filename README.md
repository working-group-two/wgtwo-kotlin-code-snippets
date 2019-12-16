# Working Group Two API examples for Kotlin

### Setup

* You will need either a personal API key or an [operator API key](https://console.wgtwo.com/api-keys-redirect).
  * Supply this client id and secret as environment variables `WGTWO_CLIENT_ID` and `WGTWO_CLIENT_SECRET`.
* You will neeed a msisdn to target on the platform (and for which you are authorized to manage)
  * Set the `MSISDN` environment variable 

### Run
Run via **command line** or an **IDE** like IntelliJ or Visual Studio Code to launch the example *Main.kt files. 

#### Via command line
**Example**
```shell script
$ WGTWO_CLIENT_ID=AbC... WGTWO_CLIENT_SECRET=dEf... MSISDN=4799900111 mvn exec:java -Dexec.mainClass="com.wgtwo.example.voicemail.VoicemailMainKt"
```
