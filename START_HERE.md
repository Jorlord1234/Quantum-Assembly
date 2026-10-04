# Quantum Assembly (version 0.4)

This is the fourth test version. It checks that the basics work.

## What is inside
- All the items and blocks, with their pictures
- The Experience Catalyst that holds liquid experience
- A creative tab called "Quantum Assembly"
- 3 recipes:
  1. Nova Smithing Template (crafting table)
  2. Uncompleted Nova Coin (smithing table)
  3. Experience Catalyst (Create Sequenced Assembly)

## How to make the mod file (the .jar)

### Easy way: GitHub builds it for you
1. Make a free account on github.com
2. Make a new repository
3. Upload ALL the files from this folder
4. Click the "Actions" tab
5. Wait for the green check mark
6. Open the finished run and download "quantum-assembly-jar"
7. Unzip it. Inside is the .jar file

### Other way: on your own computer
1. Install Java 21 (Temurin 21)
2. Open a terminal in this folder
3. Windows: type  gradlew build
   Mac or Linux: type  ./gradlew build
4. The file is in the folder  build/libs

## How to use it
Put the .jar in the mods folder of your modpack.
It needs the same pack you already have (NeoForge 1.21.1).

## If something goes wrong
Copy the first red error message and send it to Claude.
Or send the file  logs/latest.log  from your game folder.
