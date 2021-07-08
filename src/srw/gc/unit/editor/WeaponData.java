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
public class WeaponData {
    // Weapon data uses 24 bytes
    // Declarations are in the order of the structure
    // This class is basically a struct - I'm not making other 40+ gets/sets
    
    byte byte01;        // Always 00
    
    byte[] properties;  // 2 bytes (could have used a short)
    byte type;
    
    short power;
    short EN;
    
    byte part;      // mounted on head, arms, ...
    
    byte byte10;        // Always 00
    
    byte[] terrainRatings;  // 2 bytes (could have used a short)
    
    byte hit;
    byte critical;
    byte ammo;
    byte will;
    byte rangeMin;
    byte rangeMax;
    byte bgm;
    byte combo;
    
    byte byte21;        // Always 00 (probably forms a pair with the following one)
    
    byte animation;
    
    byte byte23;        // Always 00
    byte byte24;        // Always 00
    
    
    // ---------- Not part of the structure --------
    short belongToUnit;
    
    
    public void WeaponData(){
        byte01 = 0;

        properties = new byte[2];
        type = 0;

        power = 0;
        EN = 0;

        part = 0;

        byte10 = 0;

        terrainRatings = new byte[2];

        hit = 0;
        critical = 0;
        ammo = 0;
        will = 0;
        rangeMin = 0;
        rangeMax = 0;
        bgm = 0;
        combo = 0;

        byte21 = 0;

        animation = 0;

        byte23 = 0;
        byte24 = 0;
        
        belongToUnit = 0;
    }
    
    public void WeaponData(byte b01, byte[] pro, byte t, short pow, short nrg, byte par, byte b10,
            byte[] tr, byte hi, byte cri, byte amm, byte wi, byte rmin, byte rmax, byte b, byte com,
            byte b21, byte anim, byte b23, byte b24){
        
        byte01 = b01;

        properties = pro;
        type = t;

        power = pow;
        EN = nrg;

        part = par;

        byte10 = b10;

        terrainRatings = tr;

        hit = hi;
        critical = cri;
        ammo = amm;
        will = wi;
        rangeMin = rmin;
        rangeMax = rmax;
        bgm = b;
        combo = com;

        byte21 = b21;

        animation = anim;

        byte23 = b23;
        byte24 = b24;
        
        belongToUnit = 0;
    }
    
    public byte[] getBytes(){
        byte[] data = new byte[24];
        
        data[0] = byte01;
		
        data[1] = properties[0];
        data[2] = properties[1];
        data[3] = type;
        
        data[4] = (byte) ( power >> 8 & 0xff );
        data[5] = (byte) ( power & 0xff );
        data[6] = (byte) ( EN >> 8 & 0xff );
        data[7] = (byte) ( EN & 0xff );
        
        data[8] = part;
        
        data[9] = byte10;
		
        data[10] = terrainRatings[0];
        data[11] = terrainRatings[1];
        
        data[12] = hit;
        data[13] = critical;
        data[14] = ammo;
        data[15] = will;
        data[16] = rangeMin;
        data[17] = rangeMax;
        data[18] = bgm;
        data[19] = combo;
        
        data[20] = byte21;
        
        data[21] = animation;
        
        data[22] = byte23;
        data[23] = byte24;
        
        return data;
    }
    
    public String getString(){
        String line = "";
        
        line += String.valueOf(animation & 0xff) + "\t"; 
        
        if (bgm == -1)  // No BGM is actually -1, but we show 0 to give the illusion that the first entry of the combobox is selected
            line += "0\t";
        else
            line += String.valueOf(bgm & 0xff) + "\t"; 
        
        line += String.valueOf(power) + "\t";
        
        line += String.valueOf(rangeMin & 0xff) + "\t"; 
        line += String.valueOf(rangeMax & 0xff) + "\t"; 
        
        line += String.valueOf(hit) + "\t";         // Allow negative values
        line += String.valueOf(critical) + "\t";    // Allow negative values
        line += String.valueOf(ammo) + "\t";        // Allow negative values
        
        line += String.valueOf(EN) + "\t";
        
        line += String.valueOf(will & 0xff) + "\t"; 
        
        switch(part){
            case 0:     // Nowhere (shouldn't be used)
                line += "0\t";
                break;
            case 1:     // HEAD
                line += "1\t";
                break;
            case 2:     // ARMS
                line += "2\t";
                break;
            case 4:     // BODY
                line += "3\t";
                break;
            case 8:     // LEGS
                line += "4\t";
                break;
            default:     // Default to nowhere (this should never happen)
                line += "0\t";
                break;
        }
        
        line += String.valueOf(combo) + "\t";
        
        line += String.valueOf( ( terrainRatings[0] >> 4 ) & 0x0f ) + "\t";    // Space
        line += String.valueOf( terrainRatings[0] & 0x0f ) + "\t"; // Water
        line += String.valueOf( ( terrainRatings[1] >> 4 ) & 0x0f ) + "\t"; // Land
        line += String.valueOf( terrainRatings[1] & 0x0f ) + "\t";  // Air
        
        if ( ( type & 1 ) == 1 ) // Infight
            line += "X";
        line += "\t";
        if ( ( type & 2 ) == 2 ) // Gunfight
            line += "X";
        line += "\t";
        if ( ( type & 4 ) == 4 ) // ???
            line += "X";
        line += "\t";
        if ( ( type & 8 ) == 8 ) // ???
            line += "X";
        line += "\t";
        if ( ( type & 16 ) == 16 ) // Post-move
            line += "X";
        line += "\t";
        if ( ( type & 32 ) == 32 ) // Beam
            line += "X";
        line += "\t";
        if ( ( type & 64 ) == 64 ) // Missile
            line += "X";
        line += "\t";
        if ( ( type & 128 ) == 128 ) // MAP
            line += "X";
        line += "\t";
        
        if ( ( properties[1] & 1 ) == 1 ) // NT Lv1
            line += "X";
        line += "\t";
        if ( ( properties[1] & 2 ) == 2 ) // NT Lv5
            line += "X";
        line += "\t";
        if ( ( properties[1] & 4 ) == 4 ) // Lv10
            line += "X";
        line += "\t";
        if ( ( properties[1] & 8 ) == 8 ) // Lv15
            line += "X";
        line += "\t";
        if ( ( properties[1] & 16 ) == 16 ) // ???
            line += "X";
        line += "\t";
        if ( ( properties[1] & 32 ) == 32 ) // ???
            line += "X";
        line += "\t";
        if ( ( properties[1] & 64 ) == 64 ) // ???
            line += "X";
        line += "\t";
        if ( ( properties[1] & 128 ) == 128 ) // Growth 1 (S)
            line += "X";
        line += "\t";
        if ( ( properties[0] & 1 ) == 1 ) // Growth 2 (M)
            line += "X";
        line += "\t";
        if ( ( properties[0] & 2 ) == 2 ) // Growth 3 (L)
            line += "X";
        line += "\t";
        if ( ( properties[0] & 4 ) == 4 ) // Growth M (MAP)
            line += "X";
        line += "\t";
        if ( ( properties[0] & 8 ) == 8 ) // Combo
            line += "X";
        line += "\t";
        if ( ( properties[0] & 16 ) == 16 ) // Event lock
            line += "X";
        line += "\t";
        if ( ( properties[0] & 32 ) == 32 ) // Can't hit S targets
            line += "X";
        line += "\t";
        if ( ( properties[0] & 64 ) == 64 ) // Hits buildings
            line += "X";
        line += "\t";
        if ( ( properties[0] & 128 ) == 128 ) // ???
            line += "X";
        line += "\t";
        
        line += String.valueOf(byte01 & 0xff) + "\t"; 
        line += String.valueOf(byte10 & 0xff) + "\t"; 
        line += String.valueOf(byte21 & 0xff) + "\t"; 
        line += String.valueOf(byte23 & 0xff) + "\t"; 
        line += String.valueOf(byte24 & 0xff); 
        
        return line;
    }
    
    public void parseString(String line){
        String[] values = line.split("\t");
        
        // Ignore the first four columns, they're only informative
        int offset = 4;
        
        animation = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        bgm = 0;
        if (values[offset].equals("0"))
            bgm = -1;
        else
            bgm = (byte) ( Short.parseShort(values[offset]) & 0xff );
        offset++;
        
        power = Short.parseShort(values[offset]); offset++;
        rangeMin = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        rangeMax = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        hit = (byte) Short.parseShort(values[offset]); offset++;                // Allow negatives
        critical = (byte) Short.parseShort(values[offset]); offset++;           // Allow negatives
        ammo = (byte) Short.parseShort(values[offset]); offset++;               // Allow negatives
        EN = Short.parseShort(values[offset]); offset++;
        will = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        
        part = (byte) ( Short.parseShort(values[offset]) & 0xff ); 
        // We have to translate the combobox values to the ones that are actually stored in the byte
        if (part == 3)
            part = 4;
        else if (part == 4)
            part = 8;
        offset++;
        
        combo = (byte) Short.parseShort(values[offset]); offset++;              // Allow negatives
        
        terrainRatings = new byte[2]; // initialize value
        terrainRatings[0]  |= ( ( Short.parseShort(values[offset]) & 0x0f ) << 4 ); offset++;    // Space
        terrainRatings[0]  |= ( Short.parseShort(values[offset]) & 0x0f ); offset++;     // Water
        terrainRatings[1]  |= ( ( Short.parseShort(values[offset]) & 0x0f ) << 4 ); offset++;     // Land
        terrainRatings[1]  |= ( Short.parseShort(values[offset]) & 0x0f ); offset++;          // Air
		
        type = 0;    // initialize values
        if (values[offset].equals("X")) // Infight
            type |= 1;
        offset++;
        if (values[offset].equals("X")) // Gunfight
            type |= 2;
        offset++;
        if (values[offset].equals("X")) // ???
            type |= 4;
        offset++;
        if (values[offset].equals("X")) // ???
            type |= 8;
        offset++;
        if (values[offset].equals("X")) // Post-move
            type |= 16;
        offset++;
        if (values[offset].equals("X")) // Beam
            type |= 32;
        offset++;
        if (values[offset].equals("X")) // Missile
            type |= 64;
        offset++;
        if (values[offset].equals("X")) // MAP
            type |= 128;
        offset++;
		
        properties = new byte[2];    // initialize values
        if (values[offset].equals("X")) // NT Lv1
            properties[1] |= 1;
        offset++;
        if (values[offset].equals("X")) // NT Lv5
            properties[1] |= 2;
        offset++;
        if (values[offset].equals("X")) // Lv10
            properties[1] |= 4;
        offset++;
        if (values[offset].equals("X")) // Lv15
            properties[1] |= 8;
        offset++;
        if (values[offset].equals("X")) // ???
            properties[1] |= 16;
        offset++;
        if (values[offset].equals("X")) // ???
            properties[1] |= 32;
        offset++;
        if (values[offset].equals("X")) // ???
            properties[1] |= 64;
        offset++;
        if (values[offset].equals("X")) // Growth 1 (S)
            properties[1] |= 128;
        offset++;
        if (values[offset].equals("X")) // Growth 2 (M)
            properties[0] |= 1;
        offset++;
        if (values[offset].equals("X")) // Growth 3 (L)
            properties[0] |= 2;
        offset++;
        if (values[offset].equals("X")) // Growth M (MAP)
            properties[0] |= 4;
        offset++;
        if (values[offset].equals("X")) // Combo
            properties[0] |= 8;
        offset++;
        if (values[offset].equals("X")) // Event lock
            properties[0] |= 16;
        offset++;
        if (values[offset].equals("X")) // Can't hit units of size S
            properties[0] |= 32;
        offset++;
        if (values[offset].equals("X")) // Hits buildings
            properties[0] |= 64;
        offset++;
        if (values[offset].equals("X")) // ???
            properties[0] |= 128;
        offset++;
        
        byte01 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte10 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte21 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte23 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
        byte24 = (byte) ( Short.parseShort(values[offset]) & 0xff ); offset++;
    }
}
