# Reaction Tagger

Local application to run on your computer to help you manage your files and finding them quickly with tags and other fun things.

## Running locally:

You can create a local.conf in the conf directory and set the variables as you need to:

- **app.config.location**: Path to the application configuration file. If not, this will default to the directory the application is ran in. Note you can also set this configuration value by specifying the environmental property of APP_CONFIG_LOCATION at application start time.

## Notes

- On windows, to run sqlite3 (through git bash) you have to use `winpty sqlite3`