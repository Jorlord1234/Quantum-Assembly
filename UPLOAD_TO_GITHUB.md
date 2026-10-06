# How to upload to GitHub (the easy way)

GitHub's website only takes 100 files at a time. This project has about 110.
So upload it in 2 rounds. Use a NEW, EMPTY repository so no old files get mixed in.

## Round 1
Drag these into the upload box, then click "Commit changes":
- the folders  .github  and  gradle
- the files  build.gradle, gradle.properties, gradlew, gradlew.bat, settings.gradle, .gitignore, .gitattributes
- the folder  src  BUT without  src/main/resources/assets

(Easiest: use part 1 zip, it is already like this.)

## Round 2
Upload  src/main/resources/assets  (use the part 2 zip).
Keep the folder names. GitHub joins them with the ones from round 1.

## Then
1. Click "Actions" at the top
2. Wait for the green check mark (or a red cross)
3. Green: open the run, download "quantum-assembly-jar" at the bottom
4. Red: open the run, click "Summary" on the left, copy the box that says
   "The build failed" and send it to Claude

## Important
- Do NOT upload old versions on top. Old files stay in the repository and break the build.
- If you already have a repository with old stuff: make a new one, or delete the old files first.
