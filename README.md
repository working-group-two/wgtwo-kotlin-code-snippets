# Working Group Two API examples for Kotlin

### Setup

* You will need either a personal API key or an [operator API key](https://console.wgtwo.com/api-keys-redirect).
  * Supply this client id and secret as environment variables `WGTWO_CLIENT_ID` and `WGTWO_CLIENT_SECRET`.
* You will need a msisdn to target on the platform (and for which you are authorized to manage)

### Run
Run via an **IDE** like IntelliJ or Visual Studio Code to launch the example `*Main.kt` files, or via **command line**.

In any case you must supply the needed parameters as _program arguments_.

#### Command line examples

##### Setup

###### Setting the required environment variables

Before running the other commands it's helpful to set the needed credentials so you don't need to include them in each command.

```shell script
$ export WGTWO_CLIENT_ID=YOUR_CLIENT_ID
$ export WGTWO_CLIENT_SECRET=YOUR_CLIENT_SECRET
```

###### Build the project

Before running the examples you need to build the project.

```shell script
$ ./mvnw package
```

##### List and play voicemail
```shell script
$ java -jar target/wgtwo.jar voicemail list # lists all the voicemails
$ java -jar target/wgtwo.jar voicemail play <enter_voicemail_id_here> # play a voicemail by id given in the above command
```

##### Send SMS
```shell script
$ java -jar target/wgtwo.jar sendsms --from=4799900111 4799900111 This is a test SMS # sends sms to and from 4799900111
```
