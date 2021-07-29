/*
 * Copyright (C) 2021 Dashman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package srw.gc.unit.editor;

/**
 *
 * @author Jonatan
 */
public class UnitData {
    // Unit data uses 72 bytes
    // Declarations are in the order of the structure
    // This class is basically a struct - I'm not making other 120+ gets/sets
    
    byte[] abilities;   // 4 bytes (actually could use an int...)
    
    byte bodyType;
    byte headType;
    byte armsType;
    byte legsType;
    
    int bodyHP;
    int headHP;
    int armsHP;
    int legsHP;
    
    short EN;
    
    short terrainRatings;
    
    short mobility;
    short armor;
    
    short repairCost;
    short reward;
    
    byte byte37;    // Always 00
    byte byte38;    // Always 00
    byte byte39;    // Always 00
    
    byte enhanceParts;
    byte size;
    byte movement;
    byte terrainType;
    
    byte EXPmodifier;   // multiplies EXP gained by fighting this character
    
    byte[] pilotFamily; // 2 bytes (could use a short)
    
    byte bgm;
    byte shield;
    
    byte upgradesHP;
    byte upgradesEN;
    byte upgradesArmor;
    byte upgradesMobility;
    byte upgradesWeapons;
    
    byte inflationWeapons;
    
    byte capture;
    
    byte byte56;    // All units with this set to 01 seem to be units that can be piloted by mid-bosses?
    
    short item;
    byte essential;
    
    byte byte60;    // No idea
    byte byte61;    // Value of 0x80 seem to indicate it's a unit with viewable status. When they have 0xC0 (added 0x40), it seems the unit is copy of an existing one
    byte byte62;    // This is equal to the library ID, but starting from Z Gundam, its value is 1 point behind. 
                    // When it reaches Texas Mack, the value is 2 points behind the Library ID
    
    short libraryID;
    short model3D;
    short series;
    
    byte byte69;    // Always 00
    
    byte building;
    
    byte byte71;    // This and 72 seem to indicate unit order inside its own series (but some are shared?). Probably for display order in the Library?
    byte byte72;    // Dummies set these two to FF
    
    
    // ---------- Not part of the structure --------
    short[] weapons;
    
    
    public UnitData(){
        abilities = new byte[4];

        bodyType = 0;
        headType = 0;
        armsType = 0;
        legsType = 0;

        bodyHP = 0;
        headHP = 0;
        armsHP = 0;
        legsHP = 0;

        EN = 0;

        terrainRatings = 0;

        mobility = 0;
        armor = 0;

        repairCost = 0;
        reward = 0;

        byte37 = 0;
        byte38 = 0;
        byte39 = 0;

        enhanceParts = 0;
        size = 0;
        movement = 0;
        terrainType = 0;

        EXPmodifier = 0;

        pilotFamily = new byte[2];

        bgm = 0;
        shield = 0;

        upgradesHP = 0;
        upgradesEN = 0;
        upgradesArmor = 0;
        upgradesMobility = 0;
        upgradesWeapons = 0;

        inflationWeapons = 0;

        capture = 0;

        byte56 = 0;

        item = 0;
        essential = 0;

        byte60 = 0;
        byte61 = 0;
        byte62 = 0;

        libraryID = 0;
        model3D = 0;
        series = 0;

        byte69 = 0;
        
        building = 0;
        
        byte71 = 0;
        byte72 = 0;
    }
    
    public UnitData( byte[] a, byte bt, byte ht, byte at, byte lt, int bhp, int hhp, int ahp, int lhp,
            short en, short tr, short mob, short arm, short rc, short rw, byte b37, byte b38, byte b39,
            byte ep, byte s, byte mov, byte tt, byte ai, byte[] pf, byte b, byte sh, byte uhp,
            byte uen, byte uar, byte umo, byte uwe, byte inf, byte cap, byte b56, byte it, byte es,
            byte b60, byte b61, byte b62, byte lib, byte m3d, byte ser, byte b69, byte bld, byte b71, byte b72){
        
        abilities = a;

        bodyType = bt;
        headType = ht;
        armsType = at;
        legsType = lt;

        bodyHP = bhp;
        headHP = hhp;
        armsHP = ahp;
        legsHP = lhp;

        EN = en;

        terrainRatings = tr;

        mobility = mob;
        armor = arm;

        repairCost = rc;
        reward = rw;

        byte37 = b37;
        byte38 = b38;
        byte39 = b39;

        enhanceParts = ep;
        size = s;
        movement = mov;
        terrainType = tt;

        EXPmodifier = ai;

        pilotFamily = pf;

        bgm = b;
        shield = sh;

        upgradesHP = uhp;
        upgradesEN = uen;
        upgradesArmor = uar;
        upgradesMobility = umo;
        upgradesWeapons = uwe;

        inflationWeapons = inf;

        capture = cap;

        byte56 = b56;

        item = it;
        essential = es;

        byte60 = b60;
        byte61 = b61;
        byte62 = b62;

        libraryID = lib;
        model3D = m3d;
        series = ser;

        byte69 = b69;
        
        building = bld;
        
        byte71 = b71;
        byte72 = b72;
    }
    
    public byte[] getBytes(){
        byte[] data = new byte[72];
        
        data[0] = abilities[0];
        data[1] = abilities[1];
        data[2] = abilities[2];
        data[3] = abilities[3];
        
        data[4] = bodyType;
        data[5] = headType;
        data[6] = armsType;
        data[7] = legsType;
        
        data[8] = (byte) ( bodyHP >> 24 & 0xff );
        data[9] = (byte) ( bodyHP >> 16 & 0xff );
        data[10] = (byte) ( bodyHP >> 8 & 0xff );
        data[11] = (byte) ( bodyHP & 0xff );
        data[12] = (byte) ( headHP >> 24 & 0xff );
        data[13] = (byte) ( headHP >> 16 & 0xff );
        data[14] = (byte) ( headHP >> 8 & 0xff );
        data[15] = (byte) ( headHP & 0xff );
        data[16] = (byte) ( armsHP >> 24 & 0xff );
        data[17] = (byte) ( armsHP >> 16 & 0xff );
        data[18] = (byte) ( armsHP >> 8 & 0xff );
        data[19] = (byte) ( armsHP & 0xff );
        data[20] = (byte) ( legsHP >> 24 & 0xff );
        data[21] = (byte) ( legsHP >> 16 & 0xff );
        data[22] = (byte) ( legsHP >> 8 & 0xff );
        data[23] = (byte) ( legsHP & 0xff );
        
        data[24] = (byte) ( EN >> 8 & 0xff );
        data[25] = (byte) ( EN & 0xff );
        
        data[26] = (byte) ( terrainRatings >> 8 & 0xff );
        data[27] = (byte) ( terrainRatings & 0xff );
        
        data[28] = (byte) ( mobility >> 8 & 0xff );
        data[29] = (byte) ( mobility & 0xff );
        data[30] = (byte) ( armor >> 8 & 0xff );
        data[31] = (byte) ( armor & 0xff );
        
        data[32] = (byte) ( repairCost >> 8 & 0xff );
        data[33] = (byte) ( repairCost & 0xff );
        data[34] = (byte) ( reward >> 8 & 0xff );
        data[35] = (byte) ( reward & 0xff );
        
        data[36] = byte37;  // This is normal, we started counting from 1 for the naming instead of 0
        data[37] = byte38;
        data[38] = byte39;
        
        data[39] = enhanceParts;
        data[40] = size;
        data[41] = movement;
        data[42] = terrainType;
        
        data[43] = EXPmodifier;
        
        data[44] = pilotFamily[0];
        data[45] = pilotFamily[1];
        
        data[46] = bgm;
        data[47] = shield;
        
        data[48] = upgradesHP;
        data[49] = upgradesEN;
        data[50] = upgradesArmor;
        data[51] = upgradesMobility;
        data[52] = upgradesWeapons;
        
        data[53] = inflationWeapons;
        
        data[54] = capture;
        
        data[55] = byte56;
        
        data[56] = (byte) ( item >> 8 & 0xff );
        data[57] = (byte) ( item & 0xff );
        data[58] = essential;
        
        data[59] = byte60;
        data[60] = byte61;
        data[61] = byte62;
        
        data[62] = (byte) ( libraryID >> 8 & 0xff );
        data[63] = (byte) ( libraryID & 0xff );
        data[64] = (byte) ( model3D >> 8 & 0xff );
        data[65] = (byte) ( model3D & 0xff );
        data[66] = (byte) ( series >> 8 & 0xff );
        data[67] = (byte) ( series & 0xff );
        
        data[68] = byte69;
        
        data[69] = building;
        
        data[70] = byte71;
        data[71] = byte72;
        
        return data;
    }
    
    public String getString(){
        String line = "";
        
        line += String.valueOf(series) + "\t";
        line += String.valueOf(reward & 0xffff ) + "\t";
        line += String.valueOf(repairCost & 0xffff ) + "\t";
        line += String.valueOf(essential & 0xff) + "\t";    // Avoids showing -1 instead of 255
        line += String.valueOf(capture) + "\t";
        line += String.valueOf(size) + "\t";
        line += String.valueOf(enhanceParts) + "\t";
        line += String.valueOf(movement) + "\t";
        
        if ( ( building & 1 ) == 1 )
            line += "X";
        line += "\t";
        
        line += String.valueOf( ( ( terrainRatings >> 8 & 0xff ) >> 4 ) & 0x0f ) + "\t";    // Space
        line += String.valueOf( ( terrainRatings >> 8 & 0xff ) & 0x0f ) + "\t"; // Water
        line += String.valueOf( ( ( terrainRatings & 0xff ) >> 4 ) & 0x0f ) + "\t"; // Land
        line += String.valueOf( ( terrainRatings & 0xff ) & 0x0f ) + "\t";  // Air
        
        if ( ( terrainType & 1 ) == 1 ) // Air
            line += "X";
        line += "\t";
        if ( ( terrainType & 2 ) == 2 ) // Land
            line += "X";
        line += "\t";
        if ( ( terrainType & 4 ) == 4 ) // Water
            line += "X";
        line += "\t";
        if ( ( terrainType & 8 ) == 8 ) // Ground (Underground)
            line += "X";
        line += "\t";
        if ( ( terrainType & 16 ) == 16 ) // Hover
            line += "X";
        line += "\t";
        
        line += String.valueOf(bodyType) + "\t";
        line += String.valueOf(headType) + "\t";
        line += String.valueOf(armsType) + "\t";
        line += String.valueOf(legsType) + "\t";
        
        line += String.valueOf(bodyHP) + "\t";
        line += String.valueOf(headHP) + "\t";
        line += String.valueOf(armsHP) + "\t";
        line += String.valueOf(legsHP) + "\t";
        line += String.valueOf(upgradesHP) + "\t";
        
        line += String.valueOf(EN) + "\t";
        line += String.valueOf(upgradesEN) + "\t";
        line += String.valueOf(mobility) + "\t";
        line += String.valueOf(upgradesMobility) + "\t";
        line += String.valueOf(armor) + "\t";
        line += String.valueOf(upgradesArmor) + "\t";
        
        if ( ( abilities[3] & 1 ) == 1 ) // Transform
            line += "X";
        line += "\t";
        if ( ( abilities[3] & 2 ) == 2 ) // Combine
            line += "X";
        line += "\t";
        if ( ( abilities[3] & 4 ) == 4 ) // Separate
            line += "X";
        line += "\t";
        if ( ( abilities[3] & 8 ) == 8 ) // Repair
            line += "X";
        line += "\t";
        if ( ( abilities[3] & 16 ) == 16 ) // Supply
            line += "X";
        line += "\t";
        if ( ( abilities[3] & 32 ) == 32 ) // Boarding
            line += "X";
        line += "\t";
        if ( ( abilities[3] & 64 ) == 64 ) // Capture
            line += "X";
        line += "\t";
        if ( ( abilities[3] & 128 ) == 128 ) // Double Image
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 1 ) == 1 ) // Neo Getter Vision
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 2 ) == 2 ) // Shin Mach Special
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 4 ) == 4 ) // Jammer
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 8 ) == 8 ) // Beam Coat S
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 16 ) == 16 ) // Beam Coat M
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 32 ) == 32 ) // Beam Coat L
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 64 ) == 64 ) // I-Field
            line += "X";
        line += "\t";
        if ( ( abilities[2] & 128 ) == 128 ) // HP Regen S
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 1 ) == 1 ) // HP Regen M
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 2 ) == 2 ) // HP Regen L
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 4 ) == 4 ) // EN Regen S
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 8 ) == 8 ) // EN Regen M
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 16 ) == 16 ) // EN Regen L
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 32 ) == 32 ) // Mazin Power
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 64 ) == 64 ) // EWAC
            line += "X";
        line += "\t";
        if ( ( abilities[1] & 128 ) == 128 ) // V-MAX
            line += "X";
        line += "\t";
        if ( ( abilities[0] & 1 ) == 1 ) // V-MAX Red Power
            line += "X";
        line += "\t";
        if ( ( abilities[0] & 2 ) == 2 ) // V-MAXIMUM
            line += "X";
        line += "\t";
        
        if ( ( shield & 1 ) == 1 )
            line += "X";
        line += "\t";
        
        if ( ( pilotFamily[1] & 1 ) == 1 ) // Gundam
            line += "X";
        line += "\t";
        if ( ( pilotFamily[1] & 2 ) == 2 ) // L-Gaim
            line += "X";
        line += "\t";
        if ( ( pilotFamily[1] & 4 ) == 4 ) // Layzner
            line += "X";
        line += "\t";
        if ( ( pilotFamily[1] & 8 ) == 8 ) // Dragonar
            line += "X";
        line += "\t";
        if ( ( pilotFamily[1] & 16 ) == 16 ) // Mazinkaiser
            line += "X";
        line += "\t";
        if ( ( pilotFamily[1] & 32 ) == 32 ) // Getter
            line += "X";
        line += "\t";
        if ( ( pilotFamily[1] & 64 ) == 64 ) // RaijinOh
            line += "X";
        line += "\t";
        if ( ( pilotFamily[1] & 128 ) == 128 ) // Eiji
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 1 ) == 1 ) // Kaine
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 2 ) == 2 ) // Tapp
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 4 ) == 4 ) // Light
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 8 ) == 8 ) // Kouji
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 16 ) == 16 ) // Lilith
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 32 ) == 32 ) // Unused #1
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 64 ) == 64 ) // Unused #2
            line += "X";
        line += "\t";
        if ( ( pilotFamily[0] & 128 ) == 128 ) // Unused #3
            line += "X";
        line += "\t";

        line += String.valueOf(item) + "\t";
        line += String.valueOf(libraryID) + "\t";
        line += String.valueOf(model3D) + "\t";
        line += String.valueOf(EXPmodifier & 0xff) + "\t";
        line += String.valueOf(bgm) + "\t";
        
        line += String.valueOf(upgradesWeapons) + "\t";
        line += String.valueOf(inflationWeapons) + "\t";
        
        line += String.valueOf(byte37 & 0xff) + "\t";
        line += String.valueOf(byte38 & 0xff) + "\t";
        line += String.valueOf(byte39 & 0xff) + "\t";
        line += String.valueOf(byte56 & 0xff) + "\t";
        line += String.valueOf(byte60 & 0xff) + "\t";
        line += String.valueOf(byte61 & 0xff) + "\t";
        line += String.valueOf(byte62 & 0xff) + "\t";
        line += String.valueOf(byte69 & 0xff) + "\t";
        line += String.valueOf(byte71 & 0xff) + "\t";
        line += String.valueOf(byte72 & 0xff);
        
        return line;
    }
    
    public void parseString(String line){
        String[] values = line.split("\t");
        
        // Ignore the first two columns, they're only informative
        int offset = 2;
        
        series = Short.parseShort(values[offset]); offset++;
        reward = (short) ( Integer.valueOf(values[offset]) & 0xffff ); offset++;
        repairCost = (short) ( Integer.valueOf(values[offset]) & 0xffff ); offset++;
        essential = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        capture = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        size = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        enhanceParts = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        movement = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        building = 0;
        if (values[offset].equals("X"))
            building = 1;
        offset++;
        
        terrainRatings = 0; // initialize value
        terrainRatings  |= ( Short.parseShort(values[offset]) << 12 ); offset++;    // Space
        terrainRatings  |= ( Short.parseShort(values[offset]) << 8 ); offset++;     // Water
        terrainRatings  |= ( Short.parseShort(values[offset]) << 4 ); offset++;     // Land
        terrainRatings  |= ( Short.parseShort(values[offset]) ); offset++;          // Air
        
        terrainType = 0; // initialize value
        if (values[offset].equals("X")) // Air
            terrainType |= 1;
        offset++;
        if (values[offset].equals("X")) // Land
            terrainType |= 2;
        offset++;
        if (values[offset].equals("X")) // Water
            terrainType |= 4;
        offset++;
        if (values[offset].equals("X")) // Ground (Underground)
            terrainType |= 8;
        offset++;
        if (values[offset].equals("X")) // Hover
            terrainType |= 16;
        offset++;
        
        bodyType = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        headType = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        armsType = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        legsType = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        bodyHP = Integer.parseInt(values[offset]); offset++;
        headHP = Integer.parseInt(values[offset]); offset++;
        armsHP = Integer.parseInt(values[offset]); offset++;
        legsHP = Integer.parseInt(values[offset]); offset++;
        upgradesHP = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        EN = Short.parseShort(values[offset]); offset++;
        upgradesEN = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        mobility = Short.parseShort(values[offset]); offset++;
        upgradesMobility = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        armor = Short.parseShort(values[offset]); offset++;
        upgradesArmor = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        abilities = new byte[4];    // initialize values
        if (values[offset].equals("X")) // Transform
            abilities[3] |= 1;
        offset++;
        if (values[offset].equals("X")) // Combine
            abilities[3] |= 2;
        offset++;
        if (values[offset].equals("X")) // Separate
            abilities[3] |= 4;
        offset++;
        if (values[offset].equals("X")) // Repair
            abilities[3] |= 8;
        offset++;
        if (values[offset].equals("X")) // Supply
            abilities[3] |= 16;
        offset++;
        if (values[offset].equals("X")) // Boarding
            abilities[3] |= 32;
        offset++;
        if (values[offset].equals("X")) // Capture
            abilities[3] |= 64;
        offset++;
        if (values[offset].equals("X")) // Double Image
            abilities[3] |= 128;
        offset++;
        if (values[offset].equals("X")) // Neo Getter Vision
            abilities[2] |= 1;
        offset++;
        if (values[offset].equals("X")) // Shin Mach Special
            abilities[2] |= 2;
        offset++;
        if (values[offset].equals("X")) // Jammer
            abilities[2] |= 4;
        offset++;
        if (values[offset].equals("X")) // Beam Coat S
            abilities[2] |= 8;
        offset++;
        if (values[offset].equals("X")) // Beam Coat M
            abilities[2] |= 16;
        offset++;
        if (values[offset].equals("X")) // Beam Coat L
            abilities[2] |= 32;
        offset++;
        if (values[offset].equals("X")) // I-Field
            abilities[2] |= 64;
        offset++;
        if (values[offset].equals("X")) // HP Regen S
            abilities[2] |= 128;
        offset++;
        if (values[offset].equals("X")) // HP Regen M
            abilities[1] |= 1;
        offset++;
        if (values[offset].equals("X")) // HP Regen L
            abilities[1] |= 2;
        offset++;
        if (values[offset].equals("X")) // EN Regen S
            abilities[1] |= 4;
        offset++;
        if (values[offset].equals("X")) // EN Regen M
            abilities[1] |= 8;
        offset++;
        if (values[offset].equals("X")) // EN Regen L
            abilities[1] |= 16;
        offset++;
        if (values[offset].equals("X")) // Mazin Power
            abilities[1] |= 32;
        offset++;
        if (values[offset].equals("X")) // EWAC
            abilities[1] |= 64;
        offset++;
        if (values[offset].equals("X")) // V-MAX
            abilities[1] |= 128;
        offset++;
        if (values[offset].equals("X")) // V-MAX Red Power
            abilities[0] |= 1;
        offset++;
        if (values[offset].equals("X")) // V-MAXIMUM
            abilities[0] |= 2;
        offset++;
        
        shield = 0;
        if (values[offset].equals("X")) // V-MAXIMUM
            shield = 1;
        offset++;
        
        pilotFamily = new byte[2];    // initialize values
        if (values[offset].equals("X")) // Gundam
            pilotFamily[1] |= 1;
        offset++;
        if (values[offset].equals("X")) // L-Gaim
            pilotFamily[1] |= 2;
        offset++;
        if (values[offset].equals("X")) // Layzner
            pilotFamily[1] |= 4;
        offset++;
        if (values[offset].equals("X")) // Dragonar
            pilotFamily[1] |= 8;
        offset++;
        if (values[offset].equals("X")) // Mazinkaiser
            pilotFamily[1] |= 16;
        offset++;
        if (values[offset].equals("X")) // Getter
            pilotFamily[1] |= 32;
        offset++;
        if (values[offset].equals("X")) // RaijinOh
            pilotFamily[1] |= 64;
        offset++;
        if (values[offset].equals("X")) // Eiji
            pilotFamily[1] |= 128;
        offset++;
        if (values[offset].equals("X")) // Kaine
            pilotFamily[0] |= 1;
        offset++;
        if (values[offset].equals("X")) // Tapp
            pilotFamily[0] |= 2;
        offset++;
        if (values[offset].equals("X")) // Light
            pilotFamily[0] |= 4;
        offset++;
        if (values[offset].equals("X")) // Kouji
            pilotFamily[0] |= 8;
        offset++;
        if (values[offset].equals("X")) // Lilith
            pilotFamily[0] |= 16;
        offset++;
        if (values[offset].equals("X")) // Unused #1
            pilotFamily[0] |= 32;
        offset++;
        if (values[offset].equals("X")) // Unused #2
            pilotFamily[0] |= 64;
        offset++;
        if (values[offset].equals("X")) // Unused #3
            pilotFamily[0] |= 128;
        offset++;
        
        item = Short.parseShort(values[offset]); offset++;
        libraryID = Short.parseShort(values[offset]); offset++;
        model3D = Short.parseShort(values[offset]); offset++;
        EXPmodifier = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        bgm = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        upgradesWeapons = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        inflationWeapons = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        byte37 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte38 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte39 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte56 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte60 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte61 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte62 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte69 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte71 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte72 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
    }
}
