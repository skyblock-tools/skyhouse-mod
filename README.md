# Skyhouse Mod

## Running in development:

**Basic setup and authentication**
- run `gradlew genIntellijRuns`
- edit `Minecraft Client` run configuration
- add `refresh_token=your_refresh_token` to environment variables
- add `moveResources` gradle task to before launch
- add `--username <minecraft_username_or_email> --uuid <uuid> [--password <password> | --accessToken <access_token>] --userType [mojang | xbox]` to authenticate with Mojang servers