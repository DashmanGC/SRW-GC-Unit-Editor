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
public class CharacterData {
    // Character data uses 84 bytes
    // Declarations are in the order of the structure
    // This class is basically a struct - I'm not making other 150+ gets/sets
    
    byte personality;
    byte ally;          // Bit based. 01 = ally ; 02 = enemy
    
    byte melee;         // Max base value = 255
    byte ranged;        // Max base value = 255
    byte defense;       // Max base value = 255
    byte skill;         // Max base value = 255
    byte evasion;       // Max base value = 255 - shouldn't accuracy come before evasion?
    byte accuracy;      // Max base value = 255
    
    byte[] skills;      // 2 bytes (could use a short)
    
    byte ntLv1;
    byte ntLv2;
    byte ntLv3;
    byte ntLv4;
    byte ntLv5;
    byte ntLv6;
    byte ntLv7;
    byte ntLv8;
    byte ntLv9;
    
    byte potentialLv1;
    byte potentialLv2;
    byte potentialLv3;
    byte potentialLv4;
    byte potentialLv5;
    byte potentialLv6;
    byte potentialLv7;
    byte potentialLv8;
    byte potentialLv9;
    
    byte supportLv1;
    byte supportLv2;
    byte supportLv3;
    byte supportLv4;
    
    byte commandLv1;
    byte commandLv2;
    byte commandLv3;
    byte commandLv4;
    
    byte[] pilotFamily; // 2 bytes (could use a short)
    
    short skillAces;
    
    byte enemyAI;       // still not sure about this one
    
    byte parts;         // number of equippable skill parts
    
    short SP;           // Surprisingly, you can have a LOT of SP
    
    byte seishin1_lv;
    byte seishin1_id;
    short seishin1_cost;
    byte seishin2_lv;
    byte seishin2_id;
    short seishin2_cost;
    byte seishin3_lv;
    byte seishin3_id;
    short seishin3_cost;
    byte seishin4_lv;
    byte seishin4_id;
    short seishin4_cost;
    byte seishin5_lv;
    byte seishin5_id;
    short seishin5_cost;
    byte seishin6_lv;
    byte seishin6_id;
    short seishin6_cost;
    
    byte growthSchema;
    
    byte byte70;        // No idea
    byte byte71;        // Some sort of flags? Typically 80 and some alternate versions of pilots have C0 ; Gadisword's pilots have 81 (Fairey gets C1)
    byte byte72;        // Same value as the library ID
    
    short libraryID;
    short portrait;     // ID of portrait and battle lines file inside bpilot.pak
    short series;
    
    byte byte79;        // Always 00
    byte byte80;        // Playable characters and some enemies have either 01 or 02. Fairey has 03. The rest if 00
    byte byte81;        // Always 00
    byte byte82;        // Number or character inside the same robot series. Duplicates have value 00
    byte byte83;        // Always 00
    byte byte84;        // Always 00
    
    
    public void CharacterData(){
        personality = 0;
        ally = 0;

        melee = 0;
        ranged = 0;
        defense = 0;
        skill = 0; 
        evasion = 0;
        accuracy = 0;

        skills = new byte[2];

        ntLv1 = 0;
        ntLv2 = 0;
        ntLv3 = 0;
        ntLv4 = 0;
        ntLv5 = 0;
        ntLv6 = 0;
        ntLv7 = 0;
        ntLv8 = 0;
        ntLv9 = 0;

        potentialLv1 = 0;
        potentialLv2 = 0;
        potentialLv3 = 0;
        potentialLv4 = 0;
        potentialLv5 = 0;
        potentialLv6 = 0;
        potentialLv7 = 0;
        potentialLv8 = 0;
        potentialLv9 = 0;

        supportLv1 = 0;
        supportLv2 = 0;
        supportLv3 = 0;
        supportLv4 = 0;

        commandLv1 = 0;
        commandLv2 = 0;
        commandLv3 = 0;
        commandLv4 = 0;

        pilotFamily = new byte[2];

        skillAces = 0;

        enemyAI = 0;

        parts = 0;

        SP = 0;

        seishin1_lv = 0;
        seishin1_id = 0;
        seishin1_cost = 0;
        seishin2_lv = 0;
        seishin2_id = 0;
        seishin2_cost = 0;
        seishin3_lv = 0;
        seishin3_id = 0;
        seishin3_cost = 0;
        seishin4_lv = 0;
        seishin4_id = 0;
        seishin4_cost = 0;
        seishin5_lv = 0;
        seishin5_id = 0;
        seishin5_cost = 0;
        seishin6_lv = 0;
        seishin6_id = 0;
        seishin6_cost = 0;

        growthSchema = 0;

        byte70 = 0;
        byte71 = 0;
        byte72 = 0;

        libraryID = 0;
        portrait = 0;
        series = 0;

        byte79 = 0;
        byte80 = 0;
        byte81 = 0;
        byte82 = 0;
        byte83 = 0;
        byte84 = 0;
    }
    
    public void CharacterData(byte per, byte all, byte mel, byte ran, byte def, byte ski, byte eva, byte acc,
            byte[] sk, byte nl1, byte nl2, byte nl3, byte nl4, byte nl5, byte nl6, byte nl7, byte nl8, byte nl9,
            byte pl1, byte pl2, byte pl3, byte pl4, byte pl5, byte pl6, byte pl7, byte pl8, byte pl9,
            byte sl1, byte sl2, byte sl3, byte sl4, byte cl1, byte cl2, byte cl3, byte cl4, byte[] pf,
            short sa, byte ai, byte par, short s, byte s1lv, byte s1id, short s1co,
            byte s2lv, byte s2id, short s2co, byte s3lv, byte s3id, short s3co, byte s4lv, byte s4id, short s4co,
            byte s5lv, byte s5id, short s5co, byte s6lv, byte s6id, short s6co, byte gs,
            byte b70, byte b71, byte b72, short lib, short por, short ser, byte b79, 
            byte b80, byte b81, byte b82, byte b83, byte b84){
        
        personality = per;
        ally = all;

        melee = mel;
        ranged = ran;
        defense = def;
        skill = ski; 
        evasion = eva;
        accuracy = acc;

        skills = sk;

        ntLv1 = nl1;
        ntLv2 = nl2;
        ntLv3 = nl3;
        ntLv4 = nl4;
        ntLv5 = nl5;
        ntLv6 = nl6;
        ntLv7 = nl7;
        ntLv8 = nl8;
        ntLv9 = nl9;

        potentialLv1 = pl1;
        potentialLv2 = pl2;
        potentialLv3 = pl3;
        potentialLv4 = pl4;
        potentialLv5 = pl5;
        potentialLv6 = pl6;
        potentialLv7 = pl7;
        potentialLv8 = pl8;
        potentialLv9 = pl9;

        supportLv1 = sl1;
        supportLv2 = sl2;
        supportLv3 = sl3;
        supportLv4 = sl4;

        commandLv1 = cl1;
        commandLv2 = cl2;
        commandLv3 = cl3;
        commandLv4 = cl4;

        pilotFamily = pf;

        skillAces = sa;

        enemyAI = ai;

        parts = par;

        SP = s;

        seishin1_lv = s1lv;
        seishin1_id = s1id;
        seishin1_cost = s1co;
        seishin2_lv = s2lv;
        seishin2_id = s2id;
        seishin2_cost = s2co;
        seishin3_lv = s3lv;
        seishin3_id = s3id;
        seishin3_cost = s3co;
        seishin4_lv = s4lv;
        seishin4_id = s4id;
        seishin4_cost = s4co;
        seishin5_lv = s5lv;
        seishin5_id = s5id;
        seishin5_cost = s5co;
        seishin6_lv = s6lv;
        seishin6_id = s6id;
        seishin6_cost = s6co;

        growthSchema = gs;

        byte70 = b70;
        byte71 = b71;
        byte72 = b72;

        libraryID = lib;
        portrait = por;
        series = ser;

        byte79 = b79;
        byte80 = b80;
        byte81 = b81;
        byte82 = b82;
        byte83 = b83;
        byte84 = b84;
    }
    
    public byte[] getBytes(){
        byte[] data = new byte[84];
        
        data[0] = personality;
        data[1] = ally;
        
        data[2] = melee;
        data[3] = ranged;
        data[4] = defense;
        data[5] = skill;
        data[6] = evasion;
        data[7] = accuracy;
		
        data[8] = skills[0];
        data[9] = skills[1];
        
        data[10] = ntLv1;
        data[11] = ntLv2;
        data[12] = ntLv3;
        data[13] = ntLv4;
        data[14] = ntLv5;
        data[15] = ntLv6;
        data[16] = ntLv7;
        data[17] = ntLv8;
        data[18] = ntLv9;

        data[19] = potentialLv1;
        data[20] = potentialLv2;
        data[21] = potentialLv3;
        data[22] = potentialLv4;
        data[23] = potentialLv5;
        data[24] = potentialLv6;
        data[25] = potentialLv7;
        data[26] = potentialLv8;
        data[27] = potentialLv9;

        data[28] = supportLv1;
        data[29] = supportLv2;
        data[30] = supportLv3;
        data[31] = supportLv4;

        data[32] = commandLv1;
        data[33] = commandLv2;
        data[34] = commandLv3;
        data[35] = commandLv4;
        
        data[36] = pilotFamily[0];
        data[37] = pilotFamily[1];
        
        data[38] = (byte) ( skillAces >> 8 & 0xff );
        data[39] = (byte) ( skillAces & 0xff );
        
        data[40] = enemyAI;
        
        data[41] = parts;  
        
        data[42] = (byte) ( SP >> 8 & 0xff );
        data[43] = (byte) ( SP & 0xff );
        
        data[44] = seishin1_lv;
        data[45] = seishin1_id;
        data[46] = (byte) ( seishin1_cost >> 8 & 0xff );
        data[47] = (byte) ( seishin1_cost & 0xff );
        data[48] = seishin2_lv;
        data[49] = seishin2_id;
        data[50] = (byte) ( seishin2_cost >> 8 & 0xff );
        data[51] = (byte) ( seishin2_cost & 0xff );
        data[52] = seishin3_lv;
        data[53] = seishin3_id;
        data[54] = (byte) ( seishin3_cost >> 8 & 0xff );
        data[55] = (byte) ( seishin3_cost & 0xff );
        data[56] = seishin4_lv;
        data[57] = seishin4_id;
        data[58] = (byte) ( seishin4_cost >> 8 & 0xff );
        data[59] = (byte) ( seishin4_cost & 0xff );
        data[60] = seishin5_lv;
        data[61] = seishin5_id;
        data[62] = (byte) ( seishin5_cost >> 8 & 0xff );
        data[63] = (byte) ( seishin5_cost & 0xff );
        data[64] = seishin6_lv;
        data[65] = seishin6_id;
        data[66] = (byte) ( seishin6_cost >> 8 & 0xff );
        data[67] = (byte) ( seishin6_cost & 0xff );
        
        data[68] = growthSchema;
        
        data[69] = byte70;
        data[70] = byte71;
        data[71] = byte72;
        
        data[72] = (byte) ( libraryID >> 8 & 0xff );
        data[73] = (byte) ( libraryID & 0xff );
        data[74] = (byte) ( portrait >> 8 & 0xff );
        data[75] = (byte) ( portrait & 0xff );
        data[76] = (byte) ( series >> 8 & 0xff );
        data[77] = (byte) ( series & 0xff );
    
        data[78] = byte79;
        data[79] = byte80;
        data[80] = byte81;
        data[81] = byte82;
        data[82] = byte83;
        data[83] = byte84;
        
        return data;
    }
    
    public String getString(){
        String line = "";
        
        line += String.valueOf(series) + "\t";
        line += String.valueOf(ally) + "\t";
        line += String.valueOf(enemyAI & 0xff) + "\t"; 
        line += String.valueOf(personality) + "\t";
        line += String.valueOf(libraryID) + "\t"; 
        line += String.valueOf(portrait) + "\t"; 
        line += String.valueOf(parts) + "\t"; 
        line += String.valueOf(skillAces) + "\t"; 
        line += String.valueOf(growthSchema) + "\t"; 
        
        line += String.valueOf(melee & 0xff) + "\t"; 
        line += String.valueOf(ranged & 0xff) + "\t"; 
        line += String.valueOf(defense & 0xff) + "\t"; 
        line += String.valueOf(skill & 0xff) + "\t"; 
        line += String.valueOf(accuracy & 0xff) + "\t"; 
        line += String.valueOf(evasion & 0xff) + "\t"; 
        
        line += String.valueOf(ntLv1 & 0xff) + "\t"; 
        line += String.valueOf(ntLv2 & 0xff) + "\t"; 
        line += String.valueOf(ntLv3 & 0xff) + "\t"; 
        line += String.valueOf(ntLv4 & 0xff) + "\t"; 
        line += String.valueOf(ntLv5 & 0xff) + "\t"; 
        line += String.valueOf(ntLv6 & 0xff) + "\t"; 
        line += String.valueOf(ntLv7 & 0xff) + "\t"; 
        line += String.valueOf(ntLv8 & 0xff) + "\t"; 
        line += String.valueOf(ntLv9 & 0xff) + "\t"; 
        
        line += String.valueOf(potentialLv1 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv2 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv3 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv4 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv5 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv6 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv7 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv8 & 0xff) + "\t"; 
        line += String.valueOf(potentialLv9 & 0xff) + "\t"; 
        
        line += String.valueOf(supportLv1 & 0xff) + "\t"; 
        line += String.valueOf(supportLv2 & 0xff) + "\t"; 
        line += String.valueOf(supportLv3 & 0xff) + "\t"; 
        line += String.valueOf(supportLv4 & 0xff) + "\t"; 
        
        line += String.valueOf(commandLv1 & 0xff) + "\t"; 
        line += String.valueOf(commandLv2 & 0xff) + "\t"; 
        line += String.valueOf(commandLv3 & 0xff) + "\t"; 
        line += String.valueOf(commandLv4 & 0xff) + "\t"; 
        
        if ( ( skills[1] & 1 ) == 1 ) // NOT a Cyber Newtype
            line += "X";
        line += "\t";
        if ( ( skills[1] & 2 ) == 2 ) // NOT a Newtype
            line += "X";
        line += "\t";
        if ( ( skills[1] & 4 ) == 4 ) // Potential
            line += "X";
        line += "\t";
        if ( ( skills[1] & 8 ) == 8 ) // Shield Defense
            line += "X";
        line += "\t";
        if ( ( skills[1] & 16 ) == 16 ) // Support in Attack Phase
            line += "X";
        line += "\t";
        if ( ( skills[1] & 32 ) == 32 ) // Support in Defense Phase
            line += "X";
        line += "\t";
        if ( ( skills[1] & 64 ) == 64 ) // Command
            line += "X";
        line += "\t";
        if ( ( skills[1] & 128 ) == 128 ) // Instinct
            line += "X";
        line += "\t";
        if ( ( skills[0] & 1 ) == 1 ) // Counter
            line += "X";
        line += "\t";
        if ( ( skills[0] & 2 ) == 2 ) // Hit & Away
            line += "X";
        line += "\t";
        if ( ( skills[0] & 4 ) == 4 ) // Sniping
            line += "X";
        line += "\t";
        if ( ( skills[0] & 8 ) == 8 ) // Unknown #1
            line += "X";
        line += "\t";
        if ( ( skills[0] & 16 ) == 16 ) // Unknown #2
            line += "X";
        line += "\t";
        if ( ( skills[0] & 32 ) == 32 ) // Unknown #3
            line += "X";
        line += "\t";
        if ( ( skills[0] & 64 ) == 64 ) // Unknown #4
            line += "X";
        line += "\t";
        if ( ( skills[0] & 128 ) == 128 ) // Unknown #5
            line += "X";
        line += "\t";
        
        line += String.valueOf(SP) + "\t";
        
        line += String.valueOf(seishin1_id & 0xff) + "\t"; 
        line += String.valueOf(seishin1_cost) + "\t";
        line += String.valueOf(seishin1_lv & 0xff) + "\t"; 
        line += String.valueOf(seishin2_id & 0xff) + "\t"; 
        line += String.valueOf(seishin2_cost) + "\t";
        line += String.valueOf(seishin2_lv & 0xff) + "\t"; 
        line += String.valueOf(seishin3_id & 0xff) + "\t"; 
        line += String.valueOf(seishin3_cost) + "\t";
        line += String.valueOf(seishin3_lv & 0xff) + "\t"; 
        line += String.valueOf(seishin4_id & 0xff) + "\t"; 
        line += String.valueOf(seishin4_cost) + "\t";
        line += String.valueOf(seishin4_lv & 0xff) + "\t"; 
        line += String.valueOf(seishin5_id & 0xff) + "\t"; 
        line += String.valueOf(seishin5_cost) + "\t";
        line += String.valueOf(seishin5_lv & 0xff) + "\t"; 
        line += String.valueOf(seishin6_id & 0xff) + "\t"; 
        line += String.valueOf(seishin6_cost) + "\t";
        line += String.valueOf(seishin6_lv & 0xff) + "\t"; 
        
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
        
        line += String.valueOf(byte70 & 0xff) + "\t"; 
        line += String.valueOf(byte71 & 0xff) + "\t"; 
        line += String.valueOf(byte72 & 0xff) + "\t"; 
        line += String.valueOf(byte79 & 0xff) + "\t"; 
        line += String.valueOf(byte80 & 0xff) + "\t"; 
        line += String.valueOf(byte81 & 0xff) + "\t"; 
        line += String.valueOf(byte82 & 0xff) + "\t"; 
        line += String.valueOf(byte83 & 0xff) + "\t"; 
        line += String.valueOf(byte84 & 0xff);         
        
        return line;
    }
    
    public void parseString(String line){
        String[] values = line.split("\t");
        
        // Ignore the first two columns, they're only informative
        int offset = 2;
        
        series = Short.parseShort(values[offset]); offset++;
        ally = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        enemyAI = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        personality = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        libraryID = Short.parseShort(values[offset]); offset++;
        portrait = Short.parseShort(values[offset]); offset++;
        parts = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        skillAces = Short.parseShort(values[offset]); offset++;
        growthSchema = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        melee = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ranged = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        defense = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        skill = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        accuracy = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        evasion = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        ntLv1 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv2 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv3 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv4 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv5 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv6 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv7 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv8 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        ntLv9 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        potentialLv1 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv2 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv3 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv4 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv5 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv6 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv7 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv8 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        potentialLv9 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        supportLv1 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        supportLv2 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        supportLv3 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        supportLv4 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        commandLv1 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        commandLv2 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        commandLv3 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        commandLv4 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        skills = new byte[2];    // initialize values
        if (values[offset].equals("X")) // NOT a Cyber NT
            skills[1] |= 1;
        offset++;
        if (values[offset].equals("X")) // NOT a Newtype
            skills[1] |= 2;
        offset++;
        if (values[offset].equals("X")) // Potential
            skills[1] |= 4;
        offset++;
        if (values[offset].equals("X")) // Shield Defense
            skills[1] |= 8;
        offset++;
        if (values[offset].equals("X")) // Supportin Attack Phase
            skills[1] |= 16;
        offset++;
        if (values[offset].equals("X")) // Support in Defense Phase
            skills[1] |= 32;
        offset++;
        if (values[offset].equals("X")) // Command
            skills[1] |= 64;
        offset++;
        if (values[offset].equals("X")) // Instinct
            skills[1] |= 128;
        offset++;
        if (values[offset].equals("X")) // Counter
            skills[0] |= 1;
        offset++;
        if (values[offset].equals("X")) // Hit & Away
            skills[0] |= 2;
        offset++;
        if (values[offset].equals("X")) // Sniping
            skills[0] |= 4;
        offset++;
        if (values[offset].equals("X")) // Uknown #1
            skills[0] |= 8;
        offset++;
        if (values[offset].equals("X")) // Uknown #2
            skills[0] |= 16;
        offset++;
        if (values[offset].equals("X")) // Uknown #3
            skills[0] |= 32;
        offset++;
        if (values[offset].equals("X")) // Uknown #4
            skills[0] |= 64;
        offset++;
        if (values[offset].equals("X")) // Uknown #5
            skills[0] |= 128;
        offset++;
        
        SP = Short.parseShort(values[offset]); offset++;
                
        seishin1_id = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin1_cost = Short.parseShort(values[offset]); offset++;
        seishin1_lv = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin2_id = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin2_cost = Short.parseShort(values[offset]); offset++;
        seishin2_lv = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin3_id = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin3_cost = Short.parseShort(values[offset]); offset++;
        seishin3_lv = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin4_id = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin4_cost = Short.parseShort(values[offset]); offset++;
        seishin4_lv = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin5_id = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin5_cost = Short.parseShort(values[offset]); offset++;
        seishin5_lv = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin6_id = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        seishin6_cost = Short.parseShort(values[offset]); offset++;
        seishin6_lv = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
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
        
        byte70 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte71 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte72 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte79 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte80 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte81 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte82 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte83 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte84 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
    }    
}
