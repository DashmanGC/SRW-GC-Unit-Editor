SRW GC Unit Editor by Dashman
------------------------------

This program is used to edit the Unit / Weapon / Character information inside file add02dat.bin for the game Super Robot Wars GC for the Nintendo Gamecube.

The program allows the following:

- Reading the relevant data from file add02dat.bin

- Simulate max stats of Units / Weapons / Characters in the game based on the values used

- Exporting the file's data to txt files (that can be easily edited with Excel)

- Importing modified information from these txt files

- Saving the modified changes to a new add02dat.bin file


The program should be able to open add02dat.bin files from both the original and English patched version of the game.



Requirements
------------------------------

This program is a Java application. You'll need to have Java (JRE) installed for it to run. It's perfecly possible that you'll also need to install the latest JDK (version 16 at the moment) for this to run as well. If the JRE is not enough, grab the JDK from here:

https://www.oracle.com/java/technologies/javase-jdk16-downloads.html


It's recommended to run the program from the command line, like this:

	java -jar "SRW GC Unit Editor.jar"
	
This way, if the program throws any exception that I'm not controlling, you'll be able to see it in your CMD.


In order to obtain the add02dat.bin file (and later rebuild the iso with your modified version) you'll need the GCRebuilder program by bsv798, which you can find here:

https://www.romhacking.net/utilities/619/



About the program
------------------------------

It's an unoptimized mess full of beginner's mistakes that I'm not proud of. I only used Java because I know it from back in college, it's my go-to for UI applications since then and I'm too lazy to learn something new. Please don't judge me, or Swing, the program gets the job done.

That being said, I put some error messages here and there and some fields will correct you if you try to input a value bigger than what you should. I wanted to put some more of these security measures but then realized that too many events inside a class makes NetBeans want to kill itself, so I had to stop.

Here's a list of RECOMMENDATIONS that you should consider while using the program:

- The first step is always loading the add02dat.bin file. You can't do anything until that file is loaded in the program.

- As always, KEEP BACKUPS. The program shouldn't give you corrupt files, but better safe than sorry.

- Every field in this application uses numbers. Some, I managed to give an event to not allow characters in them (a-z, A-Z), others I couldn't. If you type a character in these fields, you have a 50/50 chance of generating an error. NEVER TYPE A CHARACTER INTO ANY FIELD.

- Also, no decimals allowed. To my knowledge, there are very few fields that allow negative values, so avoid those unless you're sure you can use them.

- To avoid the Russian roulette of the points aboves, edit your values in Excel. When you're happy with your changes, copy them from Excel to a txt file, and then import that file into the program.

- You can export/import three types of txt files. Respect their structure (don't add / remove columns) and don't try to import the wrong file (like importing a characters' file as a unit file), it won't work.

- If you're going to edit data in the program itself, again, run it from a CMD window so that you can see any uncatched exceptions when they occur. 

- The game has a "Safety lock" option that disables fields that either a) they're likely to break something if you change their content or b) I'm not quite sure of what they do and should probably be left alone. These fields will appear in the exported files with an * before their column name. Give it some thought before disabling the safety.

- Some of the changes you can make with this tool can break the game. Start small. Make tests to see if something breaks before spending hours changing values only to find out they don't work.

- There's a lot of numbers. Have fun. And a break if you feel you're going mad from looking at them.


About the file add02dat.bin
------------------------------

The add02dat.bin file has 123 different sections and this program allows modification of 3 of them. Of the other 120, we have: 

- Text data (that can be edited with the program SRW GC Menu Text Editor).

- Sections not worth changing due to high risk of breaking things for very little gain. Namely, the sections for assigning weapons to units and editing combo attack properties (this one also has a couple of unknown bytes).

These would in theory be interesting to change if we wanted to add new attacks, but we have no way of editing new 3D animations into the game, so it's not really worth it.

- Sections that contain unknown data.

Throghout the program, there are several comments explaining the format of the 3 sections we're modifying. Check UnitData.java, WeaponData.java and CharacterData.java if you want to know more about them.

The structure will change from game to game, but in general, all SRW games have a file like this one for storing all its info. If you're planning on making a a tool for a different game, feel free to take a look at how this program does it for ideas. You can also check SRW MXP Unit Editor in this same repository.
