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

import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jpechiba
 */
public class UserInterfaceUE extends javax.swing.JFrame {
    
    int[] block_offsets;   // Offsets at the beginning of the file (123 pointers)

    byte[][] d_blocks;  // Data blocks - we store unmodified data from add02dat.bin here (120 blocks)
    
    UnitData[] units;
    WeaponData[] weapons;
    CharacterData[] characters;
    
    int current_unit = 0;
    int current_char = 0;
    
    String[] unitNames;
    String[] weapNames;
    String[] charNames;    
    String[] commNames;
    String[] enhaNames;
    String[] skilNames;
    
    String[] series = {"Dancouga - Super Beast Machine God", "Galactic Cyclone Braiger", "Galactic Gale Baxinger", "Galactic Whirlwind Sasuraiger", 
        "Mobile Suit Gundam", "Mobile Suit Gundam 0080: War in the Pocket", "Mobile Suit Gundam: The 08th MS Team", "Mobile Suit Zeta Gundam", 
        "Mobile Suit Gundam ZZ", "Mobile Suit Gundam: Char's Counterattack", "Heavy Metal L-Gaim", "Blue Comet SPT Layzner", 
        "Metal Armor Dragonar", "Invincible Robot Trider G7", "Strongest Robot Daiohja", "Absolutely Invincible Raijin-Oh", 
        "Shin Getter Robo vs. Neo Getter Robo", "Mazinkaiser", "Future Robot Daltanious", 
        "Banpresto Originals", "ZakoMobChara (?)", "Building", "Frame (?)", "(nothing)"};
    
    String[] bgms ={"(no BGM)", "A True Kiss in Return", "Like Melos ~Lonely Way~", "To the Approaching One", "Dream Shift", 
        "Our Fighting Spirit", "Galactic Cyclone Braiger", "Khamen Khamen", "Galactic Gale Baxinger", "Letsu", 
        "Galactic Whirlwind Sasuraiger", "Try Try Try", "Trider G7's Theme", "Strongest Robot Daiohja", "Wind of No Reply", 
        "Charge, L-Gaim", "Dream-Colored Chaser", "The Red Comet", "White Base", 
        "Burning", "Soldiers of Sorrow", "Lalah", "Encounters", "Reach Out to the Sky Someday", 
        "Shine in the Storm", "Haman Gives Chase", "Activate! Double Zeta", "Beyond the Time", 
        "Storm", "Fire Wars", "Mazinkaiser's Theme", "Song of Daltanious", "Main Theme ~ The Curtains Open for the Wars", 
        "End Roll ~ Close the Curtains", "Prelude to the Battle", "Level Up", "Closed Off Future", 
        "Intermission ~ The Warriors' Resonance", "Charge the Soul of Fighters", "Splendid Star", "Real Ability", 
        "Chance of Victory", "Fierce Attack", "Madness Unleashed", "Motivated Soul",
        "Forceful Breakthrough", "Looming Menace", "The Speartip of Slaughter", "Awakening Darkness", 
        "A Moment of Tranquility", "Raid", "Faded Heart", "Persistent Anxiety", 
        "Show Your Dazzling Smile", "Nesting Strategy", "Gospel", "Cornerstone of Justice", "Evil Melody"};
    
    String lastDirectory = "."; // The last directory where a file was opened / saved
    String current_file = "";
    String current_csv = "";
    boolean file_loaded = false;
    
    int max_short = 65535;
    int max_byte = 255;
    
    
    String csv_header_units = "Unit ID\tUnit name\tRobot series\tReward\tRepair Cost\t*Essential (?)"
            + "\tCapture\tSize\tEnh. Parts\tMovement\tIs a building\tTerrain - Space\tTerrain - Water\tTerrain - Land"
            + "\tTerrain - Air\tType - Air\tType - Land\tType - Water\tType - Ground\tType - Hover"
            + "\tBody Type\tHead Type\tArms Type\tLegs Type\tBody HP\tHead HP\tArms HP\tLegs HP"
            + "\tUpgrades HP\tEnergy\tUpgrades Energy\tMobility\tUpgrades Mobility\tArmor\tUpgrades Armor"
            + "\tAbil - Transform\tAbil - Combine\tAbil - Separate\tAbil - Repair\tAbil - Supply\tAbil - Boarding"
            + "\tAbil - Capture\tAbil - Double Image\tAbil - Neo Getter Vision\tAbil - Shin Mach Special\tAbil - Jammer"
            + "\tAbil - Beam Coat S\tAbil - Beam Coat M\tAbil - Beam Coat L\tAbil - I-Field\tAbil - HP Regen S"
            + "\tAbil - HP Regen M\tAbil - HP Regen L\tAbil - EN Regen S\tAbil - EN Regen M\tAbil - EN Regen L"
            + "\tAbil - Mazin Power\tAbil - EWAC\tAbil - V-MAX\tAbil - V-MAX Red Power\tAbil - V-MAXIMUM\tAbil - Shield"
            + "\tFamily - Gundam\tFamily - L-Gaim\tFamily - Layzner\tFamily - Dragonar\tFamily - Mazinger\tFamily - Getter"
            + "\tFamily - RaijinOh\tFamily - Eiji\tFamily - Kaine\tFamily - Tapp\tFamily - Light\tFamily - Kouji"
            + "\tFamily - Lilith\tFamily - Unused#1\tFamily - Unused#2\tFamily - Unused#3\tSell item\t*Library ID\t*3D model"
            + "\tEnemy AI (?)\tBGM\tUpgrades Weapons\tInflation Weapons\t*Byte #37\t*Byte #38\t*Byte #39\t*Byte #56"
            + "\t*Byte #60\t*Byte #61\t*Byte #62\t*Byte #69\t*Byte #71\t*Byte #72\n";
    
    String csv_header_weapons = "Unit ID\tUnit name\tWeapon ID\tWeapon name\t*Animation ID\tBGM override\tBase power"
            + "\tMin range\tMax range\tHit bonus\tCrit bonus\tAmmo\tEN cost\tWill req.\tMounted on\t*Combo ID"
            + "\tTerrain - Space\tTerrain - Water\tTerrain - Land\tTerrain - Air\tType - Infight\tType - Gunfight"
            + "\t*Type - Unknown #1\t*Type - Unknown #2\tType - Post-move\tType - Beam\tType - Missile\tType - MAP"
            + "\tProp. - NT Lv1\tProp. - NT Lv5\tProp. - Lv10\tProp. - Lv15\t*Prop. - Unknown #1\t*Prop. - Unknown #2"
            + "\t*Prop. - Unknown #3\tProp. - Growth 1 (S)\tProp. - Growth 2 (M)\tProp. - Growth 3 (L)\tProp. - Growth M (MAP)"
            + "\tProp. - Combo\tProp. - Event lock\tProp. - Can't hit size S\tProp. - Hits buildings\t*Prop. - Unknown #4"
            + "\t*Byte 01\t*Byte 10\t*Byte 21\t*Byte 23\t*Byte 24\n";
    
    String csv_header_characters = "Character ID\tCharacter Name\tRobot series\tAlly / Enemy\tEnemy AI (?)\tPersonality"
            + "\t*Library ID\t*Portrait / Battle lines ID\tSkill parts\tSkill Aces\tStat growth schema\tBase Melee\tBase Ranged"
            + "\tBase Defense\tBase Skill\tBase Accuracy\tBase Evasion\tLearn NT Lv1\tLearn NT Lv2\tLearn NT Lv3\tLearn NT Lv4"
            + "\tLearn NT Lv5\tLearn NT Lv6\tLearn NT Lv7\tLearn NT Lv8\tLearn NT Lv9\tLearn Potential Lv1\tLearn Potential Lv2"
            + "\tLearn Potential Lv3\tLearn Potential Lv4\tLearn Potential Lv5\tLearn Potential Lv6\tLearn Potential Lv7"
            + "\tLearn Potential Lv8\tLearn Potential Lv9\tLearn Support Lv1\tLearn Support Lv2\tLearn Support Lv3\tLearn Support Lv4"
            + "\tLearn Command Lv1\tLearn Command Lv2\tLearn Command Lv3\tLearn Command Lv4\tSkill - NO Cyber NT\tSkill - NO Newtype"
            + "\tSkill - Potential\tSkill - Shield Defense\tSkill - Support in Attack Phase\tSkill - Support in Defense Phase"
            + "\tSkill - Command\tSkill - Instinct\tSkill - Counter\tSkill - Hit & Away\tSkill - Sniping\tSkill - Unknown #1"
            + "\tSkill - Unknown #2\tSkill - Unknown #3\tSkill - Unknown #4\tSkill - Unknown #5\tBase SP\tSp. Command ID #1"
            + "\tSp. Command Cost #1\tSp. Command Lv #1\tSp. Command ID #2\tSp. Command Cost #2\tSp. Command Lv #2\tSp. Command ID #3\tSp. Command Cost #3"
            + "\tSp. Command Lv #3\tSp. Command ID #4\tSp. Command Cost #4\tSp. Command Lv #4\tSp. Command ID #5\tSp. Command Cost #5\tSp. Command Lv #5"
            + "\tSp. Command ID #6\tSp. Command Cost #6\tSp. Command Lv #6\tFamily - Gundam\tFamily - L-Gaim\tFamily - Layzner\tFamily - Dragonar"
            + "\tFamily - Mazinger\tFamily - Getter\tFamily - RaijinOh\tFamily - Eiji\tFamily - Kaine\tFamily - Tapp\tFamily - Light"
            + "\tFamily - Kouji (Z)\tFamily - Lilith (sub)\tFamily - Unused #1\tFamily - Unused #2\tFamily - Unused #3"
            + "\t*Byte70\t*Byte71\t*Byte72\t*Byte79\t*Byte80\t*Byte81\t*Byte82\t*Byte83\t*Byte84\n";

    
    
    /**
     * Creates new form UserInterfaceUE
     */
    public UserInterfaceUE() {
        initComponents();
        
        this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("resources/icons/logo.png")).getImage());
        
        scrollWeapons.getVerticalScrollBar().setUnitIncrement(16);
        
        setValuesComboBox(series, comboSeries);
        setValuesComboBox(series, comboSeriesChar);
        setValuesComboBox(bgms, comboBGM);
        
        
        /*// Test
        WeaponPanel wp = new WeaponPanel();        
        panelWeapList.setPreferredSize(new Dimension(485,387));
        panelWeapList.removeAll();
        wp.setBounds(1, 1, 500, 385);
        panelWeapList.add(wp);
        panelWeapList.repaint();
        // End test*/
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        tabsPanel = new javax.swing.JTabbedPane();
        tabUnits = new javax.swing.JPanel();
        labelUnit = new javax.swing.JLabel();
        comboUnits = new javax.swing.JComboBox<>();
        labelSeries = new javax.swing.JLabel();
        comboSeries = new javax.swing.JComboBox<>();
        labelEssential = new javax.swing.JLabel();
        fieldEssential = new javax.swing.JTextField();
        labelReward = new javax.swing.JLabel();
        fieldReward = new javax.swing.JTextField();
        labelSell = new javax.swing.JLabel();
        fieldSell = new javax.swing.JTextField();
        labelCapture = new javax.swing.JLabel();
        comboCapture = new javax.swing.JComboBox<>();
        labelSize = new javax.swing.JLabel();
        comboSize = new javax.swing.JComboBox<>();
        labelParts = new javax.swing.JLabel();
        comboParts = new javax.swing.JComboBox<>();
        panelTerrain = new javax.swing.JPanel();
        labelRatings = new javax.swing.JLabel();
        labelSpace = new javax.swing.JLabel();
        comboSpace = new javax.swing.JComboBox<>();
        labelWater = new javax.swing.JLabel();
        comboWater = new javax.swing.JComboBox<>();
        labelLand = new javax.swing.JLabel();
        comboLand = new javax.swing.JComboBox<>();
        labelAir = new javax.swing.JLabel();
        comboAir = new javax.swing.JComboBox<>();
        labelType = new javax.swing.JLabel();
        checkAir = new javax.swing.JCheckBox();
        checkLand = new javax.swing.JCheckBox();
        checkWater = new javax.swing.JCheckBox();
        checkGround = new javax.swing.JCheckBox();
        checkHover = new javax.swing.JCheckBox();
        panelStats = new javax.swing.JPanel();
        comboBody = new javax.swing.JComboBox<>();
        labelBody = new javax.swing.JLabel();
        fieldBodyBase = new javax.swing.JTextField();
        labelBase = new javax.swing.JLabel();
        labelMax = new javax.swing.JLabel();
        fieldBodyMax = new javax.swing.JTextField();
        labelUpgrades = new javax.swing.JLabel();
        comboUpgradesHP = new javax.swing.JComboBox<>();
        comboHead = new javax.swing.JComboBox<>();
        labelHead = new javax.swing.JLabel();
        fieldHeadBase = new javax.swing.JTextField();
        fieldHeadMax = new javax.swing.JTextField();
        fieldArmsMax = new javax.swing.JTextField();
        fieldArmsBase = new javax.swing.JTextField();
        labelArms = new javax.swing.JLabel();
        comboArms = new javax.swing.JComboBox<>();
        comboLegs = new javax.swing.JComboBox<>();
        labelLegs = new javax.swing.JLabel();
        fieldLegsBase = new javax.swing.JTextField();
        fieldLegsMax = new javax.swing.JTextField();
        labelEnergy = new javax.swing.JLabel();
        fieldEnergyBase = new javax.swing.JTextField();
        fieldEnergyMax = new javax.swing.JTextField();
        comboUpgradesEN = new javax.swing.JComboBox<>();
        labelMobility = new javax.swing.JLabel();
        fieldMobilityBase = new javax.swing.JTextField();
        fieldMobilityMax = new javax.swing.JTextField();
        comboUpgradesMob = new javax.swing.JComboBox<>();
        labelArmor = new javax.swing.JLabel();
        fieldArmorBase = new javax.swing.JTextField();
        fieldArmorMax = new javax.swing.JTextField();
        comboUpgradesArmor = new javax.swing.JComboBox<>();
        labelMovement = new javax.swing.JLabel();
        comboMove = new javax.swing.JComboBox<>();
        labelRepair = new javax.swing.JLabel();
        fieldRepair = new javax.swing.JTextField();
        panelAbilities = new javax.swing.JPanel();
        checkShield = new javax.swing.JCheckBox();
        checkAbil01 = new javax.swing.JCheckBox();
        checkAbil02 = new javax.swing.JCheckBox();
        checkAbil03 = new javax.swing.JCheckBox();
        checkAbil04 = new javax.swing.JCheckBox();
        checkAbil05 = new javax.swing.JCheckBox();
        checkAbil06 = new javax.swing.JCheckBox();
        checkAbil07 = new javax.swing.JCheckBox();
        checkAbil08 = new javax.swing.JCheckBox();
        checkAbil09 = new javax.swing.JCheckBox();
        checkAbil10 = new javax.swing.JCheckBox();
        checkAbil11 = new javax.swing.JCheckBox();
        checkAbil12 = new javax.swing.JCheckBox();
        checkAbil13 = new javax.swing.JCheckBox();
        checkAbil14 = new javax.swing.JCheckBox();
        checkAbil15 = new javax.swing.JCheckBox();
        checkAbil16 = new javax.swing.JCheckBox();
        checkAbil17 = new javax.swing.JCheckBox();
        checkAbil18 = new javax.swing.JCheckBox();
        checkAbil19 = new javax.swing.JCheckBox();
        checkAbil20 = new javax.swing.JCheckBox();
        checkAbil21 = new javax.swing.JCheckBox();
        checkAbil22 = new javax.swing.JCheckBox();
        checkAbil23 = new javax.swing.JCheckBox();
        checkAbil24 = new javax.swing.JCheckBox();
        checkAbil25 = new javax.swing.JCheckBox();
        checkAbil26 = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        panelFamilyUnit = new javax.swing.JPanel();
        checkFamUnit01 = new javax.swing.JCheckBox();
        checkFamUnit02 = new javax.swing.JCheckBox();
        checkFamUnit03 = new javax.swing.JCheckBox();
        checkFamUnit04 = new javax.swing.JCheckBox();
        checkFamUnit05 = new javax.swing.JCheckBox();
        checkFamUnit06 = new javax.swing.JCheckBox();
        checkFamUnit07 = new javax.swing.JCheckBox();
        checkFamUnit08 = new javax.swing.JCheckBox();
        checkFamUnit09 = new javax.swing.JCheckBox();
        checkFamUnit10 = new javax.swing.JCheckBox();
        checkFamUnit11 = new javax.swing.JCheckBox();
        checkFamUnit12 = new javax.swing.JCheckBox();
        checkFamUnit13 = new javax.swing.JCheckBox();
        checkFamUnit14 = new javax.swing.JCheckBox();
        checkFamUnit15 = new javax.swing.JCheckBox();
        checkFamUnit16 = new javax.swing.JCheckBox();
        panelMisc = new javax.swing.JPanel();
        labelAI = new javax.swing.JLabel();
        fieldAI = new javax.swing.JTextField();
        labelLibID = new javax.swing.JLabel();
        fieldLibID = new javax.swing.JTextField();
        labelModelID = new javax.swing.JLabel();
        fieldModelID = new javax.swing.JTextField();
        labelBGM = new javax.swing.JLabel();
        comboBGM = new javax.swing.JComboBox<>();
        labelItem = new javax.swing.JLabel();
        comboItem = new javax.swing.JComboBox<>();
        panelUnknown = new javax.swing.JPanel();
        labelByte37 = new javax.swing.JLabel();
        fieldByte37 = new javax.swing.JTextField();
        labelByte38 = new javax.swing.JLabel();
        fieldByte38 = new javax.swing.JTextField();
        fieldByte39 = new javax.swing.JTextField();
        labelByte39 = new javax.swing.JLabel();
        fieldByte56 = new javax.swing.JTextField();
        labelByte56 = new javax.swing.JLabel();
        labelByte60 = new javax.swing.JLabel();
        fieldByte60 = new javax.swing.JTextField();
        labelByte61 = new javax.swing.JLabel();
        fieldByte61 = new javax.swing.JTextField();
        fieldByte62 = new javax.swing.JTextField();
        labelByte62 = new javax.swing.JLabel();
        fieldByte69 = new javax.swing.JTextField();
        labelByte69 = new javax.swing.JLabel();
        fieldByte71 = new javax.swing.JTextField();
        labelByte71 = new javax.swing.JLabel();
        fieldByte72 = new javax.swing.JTextField();
        labelByte72 = new javax.swing.JLabel();
        panelWeapons = new javax.swing.JPanel();
        labelUpgradesWeap = new javax.swing.JLabel();
        comboUpgradesWeapons = new javax.swing.JComboBox<>();
        scrollWeapons = new javax.swing.JScrollPane();
        panelWeapList = new javax.swing.JPanel();
        labelInflation = new javax.swing.JLabel();
        fieldInflation = new javax.swing.JTextField();
        labelIncrease = new javax.swing.JLabel();
        fieldIncrease = new javax.swing.JTextField();
        checkBuilding = new javax.swing.JCheckBox();
        tabCharacters = new javax.swing.JPanel();
        labelCharacter = new javax.swing.JLabel();
        labelSeriesChar = new javax.swing.JLabel();
        comboChars = new javax.swing.JComboBox<>();
        comboSeriesChar = new javax.swing.JComboBox<>();
        labelPersonality = new javax.swing.JLabel();
        labelLibIDChar = new javax.swing.JLabel();
        comboPersonality = new javax.swing.JComboBox<>();
        fieldLibIDChar = new javax.swing.JTextField();
        labelAlly = new javax.swing.JLabel();
        comboAlly = new javax.swing.JComboBox<>();
        labelSkillParts = new javax.swing.JLabel();
        comboSkillParts = new javax.swing.JComboBox<>();
        labelEnemyAI = new javax.swing.JLabel();
        fieldEnemyAI = new javax.swing.JTextField();
        labelPortrait = new javax.swing.JLabel();
        fieldPortrait = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        panelFamilyChar = new javax.swing.JPanel();
        checkFamChar01 = new javax.swing.JCheckBox();
        checkFamChar02 = new javax.swing.JCheckBox();
        checkFamChar03 = new javax.swing.JCheckBox();
        checkFamChar04 = new javax.swing.JCheckBox();
        checkFamChar05 = new javax.swing.JCheckBox();
        checkFamChar06 = new javax.swing.JCheckBox();
        checkFamChar07 = new javax.swing.JCheckBox();
        checkFamChar08 = new javax.swing.JCheckBox();
        checkFamChar09 = new javax.swing.JCheckBox();
        checkFamChar10 = new javax.swing.JCheckBox();
        checkFamChar11 = new javax.swing.JCheckBox();
        checkFamChar12 = new javax.swing.JCheckBox();
        checkFamChar13 = new javax.swing.JCheckBox();
        checkFamChar14 = new javax.swing.JCheckBox();
        checkFamChar15 = new javax.swing.JCheckBox();
        checkFamChar16 = new javax.swing.JCheckBox();
        labelGrowthSchema = new javax.swing.JLabel();
        comboGrowthSchema = new javax.swing.JComboBox<>();
        panelStatsChar = new javax.swing.JPanel();
        labelHeader1 = new javax.swing.JLabel();
        fieldMeleeBase = new javax.swing.JTextField();
        labelMelee = new javax.swing.JLabel();
        fieldMeleeMax = new javax.swing.JTextField();
        labelMeleeRank = new javax.swing.JLabel();
        labelDefense = new javax.swing.JLabel();
        fieldDefenseBase = new javax.swing.JTextField();
        fieldDefenseMax = new javax.swing.JTextField();
        labelDefenseRank = new javax.swing.JLabel();
        fieldAccuracyBase = new javax.swing.JTextField();
        labelAccuracyRank = new javax.swing.JLabel();
        fieldAccuracyMax = new javax.swing.JTextField();
        labelAccuracy = new javax.swing.JLabel();
        labelRanged = new javax.swing.JLabel();
        fieldRangedBase = new javax.swing.JTextField();
        fieldRangedMax = new javax.swing.JTextField();
        labelRangedRank = new javax.swing.JLabel();
        labelSkill = new javax.swing.JLabel();
        fieldSkillBase = new javax.swing.JTextField();
        fieldSkillMax = new javax.swing.JTextField();
        labelSkillRank = new javax.swing.JLabel();
        labelEvasion = new javax.swing.JLabel();
        fieldEvasionBase = new javax.swing.JTextField();
        fieldEvasionMax = new javax.swing.JTextField();
        labelEvasionRank = new javax.swing.JLabel();
        panelSkillsLevel = new javax.swing.JPanel();
        labelHeader2 = new javax.swing.JLabel();
        labelNTlevel = new javax.swing.JLabel();
        fieldNTlv1 = new javax.swing.JTextField();
        fieldNTlv2 = new javax.swing.JTextField();
        fieldNTlv3 = new javax.swing.JTextField();
        fieldNTlv4 = new javax.swing.JTextField();
        fieldNTlv5 = new javax.swing.JTextField();
        fieldNTlv6 = new javax.swing.JTextField();
        fieldNTlv7 = new javax.swing.JTextField();
        fieldNTlv8 = new javax.swing.JTextField();
        fieldNTlv9 = new javax.swing.JTextField();
        labelPotentialLevel = new javax.swing.JLabel();
        fieldPotentialLv1 = new javax.swing.JTextField();
        fieldPotentialLv2 = new javax.swing.JTextField();
        fieldPotentialLv3 = new javax.swing.JTextField();
        fieldPotentialLv4 = new javax.swing.JTextField();
        fieldPotentialLv5 = new javax.swing.JTextField();
        fieldPotentialLv6 = new javax.swing.JTextField();
        fieldPotentialLv7 = new javax.swing.JTextField();
        fieldPotentialLv8 = new javax.swing.JTextField();
        fieldPotentialLv9 = new javax.swing.JTextField();
        labelSupportLevel = new javax.swing.JLabel();
        fieldSupportLv1 = new javax.swing.JTextField();
        fieldSupportLv2 = new javax.swing.JTextField();
        fieldSupportLv3 = new javax.swing.JTextField();
        fieldSupportLv4 = new javax.swing.JTextField();
        labelCommandLevel = new javax.swing.JLabel();
        fieldCommandLv1 = new javax.swing.JTextField();
        fieldCommandLv2 = new javax.swing.JTextField();
        fieldCommandLv3 = new javax.swing.JTextField();
        fieldCommandLv4 = new javax.swing.JTextField();
        panelSkills = new javax.swing.JPanel();
        checkSkill01 = new javax.swing.JCheckBox();
        checkSkill02 = new javax.swing.JCheckBox();
        checkSkill03 = new javax.swing.JCheckBox();
        checkSkill04 = new javax.swing.JCheckBox();
        checkSkill05 = new javax.swing.JCheckBox();
        checkSkill06 = new javax.swing.JCheckBox();
        checkSkill07 = new javax.swing.JCheckBox();
        checkSkill08 = new javax.swing.JCheckBox();
        checkSkill09 = new javax.swing.JCheckBox();
        checkSkill10 = new javax.swing.JCheckBox();
        checkSkill11 = new javax.swing.JCheckBox();
        checkSkill12 = new javax.swing.JCheckBox();
        checkSkill13 = new javax.swing.JCheckBox();
        checkSkill14 = new javax.swing.JCheckBox();
        checkSkill15 = new javax.swing.JCheckBox();
        checkSkill16 = new javax.swing.JCheckBox();
        panelSpirit = new javax.swing.JPanel();
        labelHeader3 = new javax.swing.JLabel();
        labelSP = new javax.swing.JLabel();
        fieldSPBase = new javax.swing.JTextField();
        fieldSPMax = new javax.swing.JTextField();
        labelSPRank = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        labelHeader4 = new javax.swing.JLabel();
        labelCommand1 = new javax.swing.JLabel();
        comboCommand1 = new javax.swing.JComboBox<>();
        fieldCostCommand1 = new javax.swing.JTextField();
        fieldLearnCommand1 = new javax.swing.JTextField();
        labelCommand2 = new javax.swing.JLabel();
        comboCommand2 = new javax.swing.JComboBox<>();
        fieldCostCommand2 = new javax.swing.JTextField();
        fieldLearnCommand2 = new javax.swing.JTextField();
        labelCommand3 = new javax.swing.JLabel();
        comboCommand3 = new javax.swing.JComboBox<>();
        fieldCostCommand3 = new javax.swing.JTextField();
        fieldLearnCommand3 = new javax.swing.JTextField();
        labelCommand4 = new javax.swing.JLabel();
        comboCommand4 = new javax.swing.JComboBox<>();
        fieldCostCommand4 = new javax.swing.JTextField();
        fieldLearnCommand4 = new javax.swing.JTextField();
        labelCommand5 = new javax.swing.JLabel();
        comboCommand5 = new javax.swing.JComboBox<>();
        fieldCostCommand5 = new javax.swing.JTextField();
        fieldLearnCommand5 = new javax.swing.JTextField();
        labelCommand6 = new javax.swing.JLabel();
        comboCommand6 = new javax.swing.JComboBox<>();
        fieldCostCommand6 = new javax.swing.JTextField();
        fieldLearnCommand6 = new javax.swing.JTextField();
        panelUnknownChar = new javax.swing.JPanel();
        labelByteChar70 = new javax.swing.JLabel();
        fieldByteChar70 = new javax.swing.JTextField();
        labelByteChar71 = new javax.swing.JLabel();
        fieldByteChar71 = new javax.swing.JTextField();
        labelByteChar72 = new javax.swing.JLabel();
        fieldByteChar72 = new javax.swing.JTextField();
        fieldByteChar84 = new javax.swing.JTextField();
        fieldByteChar81 = new javax.swing.JTextField();
        labelByteChar81 = new javax.swing.JLabel();
        labelByteChar82 = new javax.swing.JLabel();
        fieldByteChar80 = new javax.swing.JTextField();
        labelByteChar79 = new javax.swing.JLabel();
        labelByteChar80 = new javax.swing.JLabel();
        fieldByteChar83 = new javax.swing.JTextField();
        fieldByteChar82 = new javax.swing.JTextField();
        labelByteChar83 = new javax.swing.JLabel();
        labelByteChar84 = new javax.swing.JLabel();
        fieldByteChar79 = new javax.swing.JTextField();
        panelPersonality = new javax.swing.JPanel();
        labelHit = new javax.swing.JLabel();
        fieldPersHit = new javax.swing.JTextField();
        labelMiss = new javax.swing.JLabel();
        fieldPersMiss = new javax.swing.JTextField();
        labelEvade = new javax.swing.JLabel();
        fieldPersEvade = new javax.swing.JTextField();
        labelDamage = new javax.swing.JLabel();
        fieldPersDamage = new javax.swing.JTextField();
        labelEnemyKill = new javax.swing.JLabel();
        fieldPersEnemy = new javax.swing.JTextField();
        labelAllyKill = new javax.swing.JLabel();
        fieldPersAlly = new javax.swing.JTextField();
        labelSkillAces = new javax.swing.JLabel();
        comboSkillAces = new javax.swing.JComboBox<>();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        itemOpenBin = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuExport = new javax.swing.JMenu();
        itemUnitsExport = new javax.swing.JMenuItem();
        itemWeaponsExport = new javax.swing.JMenuItem();
        itemCharactersExport = new javax.swing.JMenuItem();
        menuImport = new javax.swing.JMenu();
        itemUnitsImport = new javax.swing.JMenuItem();
        itemWeaponsImport = new javax.swing.JMenuItem();
        itemCharactersImport = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        itemSaveBin = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        itemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        checkItemSafety = new javax.swing.JCheckBoxMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SRW GC Unit & Character Editor v0.9");
        setPreferredSize(new java.awt.Dimension(1055, 760));
        setResizable(false);

        tabsPanel.setPreferredSize(new java.awt.Dimension(1052, 720));

        tabUnits.setPreferredSize(new java.awt.Dimension(1047, 700));

        labelUnit.setText("Unit:");

        comboUnits.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));
        comboUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUnitsActionPerformed(evt);
            }
        });

        labelSeries.setText("Robot series:");

        comboSeries.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        labelEssential.setText("Essential (?)");
        labelEssential.setToolTipText("");

        fieldEssential.setEditable(false);
        fieldEssential.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldEssential.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldEssentialKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldEssentialKeyTyped(evt);
            }
        });

        labelReward.setText("Reward");

        fieldReward.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldReward.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldRewardKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldRewardKeyTyped(evt);
            }
        });

        labelSell.setText("Sell value");

        fieldSell.setEditable(false);
        fieldSell.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelCapture.setText("Capture");

        comboCapture.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Capture only", "Capture & use" }));

        labelSize.setText("Size");

        comboSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SS", "S", "M", "L", "LL" }));

        labelParts.setText("Enhance parts");

        comboParts.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4" }));

        panelTerrain.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Terrain data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 102, 255))); // NOI18N

        labelRatings.setText("Ratings:");

        labelSpace.setText("Space");

        comboSpace.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "S", "A", "B", "C" }));

        labelWater.setText("Water");

        comboWater.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "S", "A", "B", "C" }));

        labelLand.setText("Land");

        comboLand.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "S", "A", "B", "C" }));

        labelAir.setText("Air");

        comboAir.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "S", "A", "B", "C" }));

        labelType.setText("Type:");

        checkAir.setText("Air");

        checkLand.setText("Land");

        checkWater.setText("Water");

        checkGround.setText("Ground");

        checkHover.setText("Hover");

        javax.swing.GroupLayout panelTerrainLayout = new javax.swing.GroupLayout(panelTerrain);
        panelTerrain.setLayout(panelTerrainLayout);
        panelTerrainLayout.setHorizontalGroup(
            panelTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTerrainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelRatings)
                    .addComponent(labelType))
                .addGap(18, 18, 18)
                .addGroup(panelTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTerrainLayout.createSequentialGroup()
                        .addComponent(labelSpace, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboSpace, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelWater, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboWater, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelLand, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboLand, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelAir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboAir, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTerrainLayout.createSequentialGroup()
                        .addComponent(checkAir, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkLand, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkWater, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkGround, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkHover, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTerrainLayout.setVerticalGroup(
            panelTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTerrainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelRatings)
                    .addComponent(labelSpace)
                    .addComponent(comboSpace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelWater)
                    .addComponent(comboWater, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLand)
                    .addComponent(comboLand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAir)
                    .addComponent(comboAir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTerrainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelType)
                    .addComponent(checkAir)
                    .addComponent(checkLand)
                    .addComponent(checkWater)
                    .addComponent(checkGround)
                    .addComponent(checkHover))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelStats.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stats", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 102, 255))); // NOI18N

        comboBody.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unused", "Robot", "Ship" }));
        comboBody.setSelectedIndex(1);
        comboBody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBodyActionPerformed(evt);
            }
        });

        labelBody.setText("BODY HP");

        fieldBodyBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldBodyBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldBodyBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldBodyBaseKeyTyped(evt);
            }
        });

        labelBase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBase.setText("Base value");

        labelMax.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelMax.setText("Max value");

        fieldBodyMax.setEditable(false);
        fieldBodyMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelUpgrades.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelUpgrades.setText("Upgrades");

        comboUpgradesHP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        comboUpgradesHP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUpgradesHPActionPerformed(evt);
            }
        });

        comboHead.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unused", "Robot", "Ship" }));
        comboHead.setSelectedIndex(1);
        comboHead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboHeadActionPerformed(evt);
            }
        });

        labelHead.setText("HEAD HP");

        fieldHeadBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldHeadBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldHeadBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldHeadBaseKeyTyped(evt);
            }
        });

        fieldHeadMax.setEditable(false);
        fieldHeadMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        fieldArmsMax.setEditable(false);
        fieldArmsMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        fieldArmsBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldArmsBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldArmsBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldArmsBaseKeyTyped(evt);
            }
        });

        labelArms.setText("ARMS HP");

        comboArms.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unused", "Robot", "Ship" }));
        comboArms.setSelectedIndex(1);
        comboArms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboArmsActionPerformed(evt);
            }
        });

        comboLegs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unused", "Robot", "Ship" }));
        comboLegs.setSelectedIndex(1);
        comboLegs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboLegsActionPerformed(evt);
            }
        });

        labelLegs.setText("LEGS HP");

        fieldLegsBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLegsBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLegsBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldLegsBaseKeyTyped(evt);
            }
        });

        fieldLegsMax.setEditable(false);
        fieldLegsMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelEnergy.setText("Energy");

        fieldEnergyBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldEnergyBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldEnergyBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldEnergyBaseKeyTyped(evt);
            }
        });

        fieldEnergyMax.setEditable(false);
        fieldEnergyMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        comboUpgradesEN.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        comboUpgradesEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUpgradesENActionPerformed(evt);
            }
        });

        labelMobility.setText("Mobility");

        fieldMobilityBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldMobilityBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldMobilityBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldMobilityBaseKeyTyped(evt);
            }
        });

        fieldMobilityMax.setEditable(false);
        fieldMobilityMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        comboUpgradesMob.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        comboUpgradesMob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUpgradesMobActionPerformed(evt);
            }
        });

        labelArmor.setText("Armor");

        fieldArmorBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldArmorBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldArmorBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldArmorBaseKeyTyped(evt);
            }
        });

        fieldArmorMax.setEditable(false);
        fieldArmorMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        comboUpgradesArmor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        comboUpgradesArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUpgradesArmorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelStatsLayout = new javax.swing.GroupLayout(panelStats);
        panelStats.setLayout(panelStatsLayout);
        panelStatsLayout.setHorizontalGroup(
            panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboArms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboHead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboLegs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelStatsLayout.createSequentialGroup()
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelBody, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelHead, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelArms, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelLegs, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelEnergy, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelMobility, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelStatsLayout.createSequentialGroup()
                                .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(fieldLegsBase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fieldArmsBase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fieldHeadBase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fieldBodyBase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelBase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(fieldEnergyBase, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelStatsLayout.createSequentialGroup()
                                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(labelMax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(fieldBodyMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(fieldEnergyMax, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(labelUpgrades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(comboUpgradesHP, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(comboUpgradesEN, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(fieldHeadMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLegsMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldArmsMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelStatsLayout.createSequentialGroup()
                                .addComponent(fieldMobilityBase, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fieldMobilityMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboUpgradesMob, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelStatsLayout.createSequentialGroup()
                        .addComponent(labelArmor, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(fieldArmorBase, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldArmorMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboUpgradesArmor, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelStatsLayout.setVerticalGroup(
            panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatsLayout.createSequentialGroup()
                .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelStatsLayout.createSequentialGroup()
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelMax)
                            .addComponent(labelUpgrades))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fieldBodyMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboUpgradesHP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldHeadMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldArmsMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldLegsMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fieldEnergyMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboUpgradesEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelStatsLayout.createSequentialGroup()
                        .addComponent(labelBase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBody, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelBody)
                            .addComponent(fieldBodyBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboHead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelHead)
                            .addComponent(fieldHeadBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboArms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelArms)
                            .addComponent(fieldArmsBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboLegs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelLegs)
                            .addComponent(fieldLegsBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fieldEnergyBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelEnergy))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMobility)
                    .addComponent(fieldMobilityMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboUpgradesMob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldMobilityBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelArmor)
                    .addComponent(fieldArmorMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboUpgradesArmor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldArmorBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelMovement.setText("Movement");

        comboMove.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));

        labelRepair.setText("Repair cost");

        fieldRepair.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldRepair.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldRepairKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldRepairKeyTyped(evt);
            }
        });

        panelAbilities.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Unit abilities", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 102, 255))); // NOI18N

        checkShield.setText("Unit w/ Shield");

        checkAbil01.setText("Transform");

        checkAbil02.setText("Combine");

        checkAbil03.setText("Separate");

        checkAbil04.setText("Repair");

        checkAbil05.setText("Supply");

        checkAbil06.setText("Boarding");

        checkAbil07.setText("Capture");

        checkAbil08.setText("Double Image");

        checkAbil09.setText("Neo Getter Vision");

        checkAbil10.setText("Shin Mach Special");

        checkAbil11.setLabel("Jammer");

        checkAbil12.setLabel("Beam Coat S");

        checkAbil13.setText("Beam Coat M");

        checkAbil14.setText("Beam Coat L");

        checkAbil15.setText("I-Field");

        checkAbil16.setText("HP Regen S");

        checkAbil17.setText("HP Regen M");

        checkAbil18.setText("HP Regen L");

        checkAbil19.setText("EN Regen S");

        checkAbil20.setText("EN Regen M");

        checkAbil21.setText("EN Regen L");

        checkAbil22.setText("Mazin Power");

        checkAbil23.setText("EWAC");

        checkAbil24.setText("V-MAX");

        checkAbil25.setText("V-MAX Red P.");

        checkAbil26.setText("V-MAXIMUM");

        javax.swing.GroupLayout panelAbilitiesLayout = new javax.swing.GroupLayout(panelAbilities);
        panelAbilities.setLayout(panelAbilitiesLayout);
        panelAbilitiesLayout.setHorizontalGroup(
            panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAbilitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAbilitiesLayout.createSequentialGroup()
                        .addComponent(checkAbil07, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil14, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil21, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkShield, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAbilitiesLayout.createSequentialGroup()
                        .addComponent(checkAbil06, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil13, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil20, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAbilitiesLayout.createSequentialGroup()
                        .addComponent(checkAbil05, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil12, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil19, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil26, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAbilitiesLayout.createSequentialGroup()
                        .addComponent(checkAbil02, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil09, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil16, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil23, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAbilitiesLayout.createSequentialGroup()
                        .addComponent(checkAbil03, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil10, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil17, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil24, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAbilitiesLayout.createSequentialGroup()
                        .addComponent(checkAbil04, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil11, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil18, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil25, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAbilitiesLayout.createSequentialGroup()
                        .addComponent(checkAbil01, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil08, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil15, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkAbil22, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelAbilitiesLayout.setVerticalGroup(
            panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAbilitiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAbil01)
                    .addComponent(checkAbil08)
                    .addComponent(checkAbil15)
                    .addComponent(checkAbil22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAbil02)
                    .addComponent(checkAbil09)
                    .addComponent(checkAbil16)
                    .addComponent(checkAbil23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAbil03)
                    .addComponent(checkAbil10)
                    .addComponent(checkAbil17)
                    .addComponent(checkAbil24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAbil04)
                    .addComponent(checkAbil11)
                    .addComponent(checkAbil18)
                    .addComponent(checkAbil25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAbil05)
                    .addComponent(checkAbil12)
                    .addComponent(checkAbil19)
                    .addComponent(checkAbil26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAbil06)
                    .addComponent(checkAbil13)
                    .addComponent(checkAbil20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelAbilitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkAbil07)
                    .addComponent(checkAbil14)
                    .addComponent(checkAbil21)
                    .addComponent(checkShield))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        panelFamilyUnit.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pilot family", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 102, 255))); // NOI18N

        checkFamUnit01.setText("Gundam");

        checkFamUnit02.setText("L-Gaim");

        checkFamUnit03.setText("Layzner");

        checkFamUnit04.setText("Dragonar");

        checkFamUnit05.setText("Mazinger");

        checkFamUnit06.setText("Getter");

        checkFamUnit07.setText("RaijinOh");

        checkFamUnit08.setText("Eiji");

        checkFamUnit09.setText("Kaine");

        checkFamUnit10.setText("Tapp");

        checkFamUnit11.setText("Light");

        checkFamUnit12.setText("Kouji (Z)");

        checkFamUnit13.setText("Lilith (sub)");

        checkFamUnit14.setText("Unused #1");

        checkFamUnit15.setText("Unused #2");

        checkFamUnit16.setText("Unused #3");

        javax.swing.GroupLayout panelFamilyUnitLayout = new javax.swing.GroupLayout(panelFamilyUnit);
        panelFamilyUnit.setLayout(panelFamilyUnitLayout);
        panelFamilyUnitLayout.setHorizontalGroup(
            panelFamilyUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFamilyUnitLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFamilyUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFamilyUnitLayout.createSequentialGroup()
                        .addComponent(checkFamUnit01, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit05, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit09, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFamilyUnitLayout.createSequentialGroup()
                        .addComponent(checkFamUnit02, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit06, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFamilyUnitLayout.createSequentialGroup()
                        .addComponent(checkFamUnit03, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit07, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit15, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFamilyUnitLayout.createSequentialGroup()
                        .addComponent(checkFamUnit04, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit08, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit12, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamUnit16, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFamilyUnitLayout.setVerticalGroup(
            panelFamilyUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFamilyUnitLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFamilyUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamUnit01)
                    .addComponent(checkFamUnit05)
                    .addComponent(checkFamUnit09)
                    .addComponent(checkFamUnit13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFamilyUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamUnit02)
                    .addComponent(checkFamUnit06)
                    .addComponent(checkFamUnit10)
                    .addComponent(checkFamUnit14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFamilyUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamUnit03)
                    .addComponent(checkFamUnit07)
                    .addComponent(checkFamUnit11)
                    .addComponent(checkFamUnit15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFamilyUnitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamUnit04)
                    .addComponent(checkFamUnit08)
                    .addComponent(checkFamUnit12)
                    .addComponent(checkFamUnit16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelMisc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Miscellaneous", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 102, 255))); // NOI18N

        labelAI.setText("Enemy AI (?)");

        fieldAI.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldAI.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldAIKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldAIKeyTyped(evt);
            }
        });

        labelLibID.setText("Library ID");

        fieldLibID.setEditable(false);
        fieldLibID.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLibID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLibIDKeyReleased(evt);
            }
        });

        labelModelID.setText("3D model ID");

        fieldModelID.setEditable(false);
        fieldModelID.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldModelID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldModelIDKeyReleased(evt);
            }
        });

        labelBGM.setText("BGM");

        comboBGM.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        labelItem.setText("Sell item");

        comboItem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        javax.swing.GroupLayout panelMiscLayout = new javax.swing.GroupLayout(panelMisc);
        panelMisc.setLayout(panelMiscLayout);
        panelMiscLayout.setHorizontalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMiscLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMiscLayout.createSequentialGroup()
                        .addComponent(labelLibID, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldLibID, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelModelID, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldModelID, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelAI, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldAI, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMiscLayout.createSequentialGroup()
                        .addComponent(labelBGM, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBGM, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMiscLayout.createSequentialGroup()
                        .addComponent(labelItem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboItem, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMiscLayout.setVerticalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMiscLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelItem)
                    .addComponent(comboItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLibID)
                    .addComponent(fieldLibID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelModelID)
                    .addComponent(fieldModelID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAI)
                    .addComponent(fieldAI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBGM)
                    .addComponent(comboBGM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelUnknown.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Unknown bytes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 0, 51))); // NOI18N

        labelByte37.setText("#37");
        labelByte37.setToolTipText("");

        fieldByte37.setEditable(false);
        fieldByte37.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte38.setText("#38");
        labelByte38.setToolTipText("");

        fieldByte38.setEditable(false);
        fieldByte38.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        fieldByte39.setEditable(false);
        fieldByte39.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte39.setText("#39");
        labelByte39.setToolTipText("");

        fieldByte56.setEditable(false);
        fieldByte56.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte56.setText("#56");

        labelByte60.setText("#60");

        fieldByte60.setEditable(false);
        fieldByte60.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte61.setText("#61");

        fieldByte61.setEditable(false);
        fieldByte61.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        fieldByte62.setEditable(false);
        fieldByte62.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte62.setText("#62");

        fieldByte69.setEditable(false);
        fieldByte69.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte69.setText("#69");

        fieldByte71.setEditable(false);
        fieldByte71.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte71.setText("#71");

        fieldByte72.setEditable(false);
        fieldByte72.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByte72.setText("#72");

        javax.swing.GroupLayout panelUnknownLayout = new javax.swing.GroupLayout(panelUnknown);
        panelUnknown.setLayout(panelUnknownLayout);
        panelUnknownLayout.setHorizontalGroup(
            panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUnknownLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownLayout.createSequentialGroup()
                            .addComponent(labelByte37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(fieldByte37, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownLayout.createSequentialGroup()
                            .addComponent(labelByte38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(fieldByte38, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownLayout.createSequentialGroup()
                            .addComponent(labelByte39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(fieldByte39, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelUnknownLayout.createSequentialGroup()
                        .addComponent(labelByte56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldByte56, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownLayout.createSequentialGroup()
                            .addComponent(labelByte60, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(fieldByte60, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownLayout.createSequentialGroup()
                            .addComponent(labelByte61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(fieldByte61, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownLayout.createSequentialGroup()
                            .addComponent(labelByte62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(fieldByte62, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUnknownLayout.createSequentialGroup()
                        .addComponent(labelByte69, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldByte69, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelUnknownLayout.createSequentialGroup()
                        .addComponent(labelByte71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldByte71, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelUnknownLayout.createSequentialGroup()
                        .addComponent(labelByte72, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldByte72, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        panelUnknownLayout.setVerticalGroup(
            panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUnknownLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelByte37)
                    .addComponent(fieldByte37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelByte69)
                    .addComponent(fieldByte69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelByte38)
                    .addComponent(fieldByte38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelByte39)
                    .addComponent(fieldByte39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelByte71)
                    .addComponent(fieldByte71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelByte72)
                        .addComponent(fieldByte72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelUnknownLayout.createSequentialGroup()
                        .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByte56)
                            .addComponent(fieldByte56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByte60)
                            .addComponent(fieldByte60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByte61)
                            .addComponent(fieldByte61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByte62)
                            .addComponent(fieldByte62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelWeapons.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Weapons", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 102, 255))); // NOI18N

        labelUpgradesWeap.setText("Upgrades");

        comboUpgradesWeapons.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        comboUpgradesWeapons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUpgradesWeaponsActionPerformed(evt);
            }
        });

        panelWeapList.setPreferredSize(new java.awt.Dimension(485, 321));

        javax.swing.GroupLayout panelWeapListLayout = new javax.swing.GroupLayout(panelWeapList);
        panelWeapList.setLayout(panelWeapListLayout);
        panelWeapListLayout.setHorizontalGroup(
            panelWeapListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 536, Short.MAX_VALUE)
        );
        panelWeapListLayout.setVerticalGroup(
            panelWeapListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 337, Short.MAX_VALUE)
        );

        scrollWeapons.setViewportView(panelWeapList);

        labelInflation.setText("Inflation");

        fieldInflation.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldInflation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldInflationKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldInflationKeyTyped(evt);
            }
        });

        labelIncrease.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelIncrease.setText("increases upgrade cost by");

        fieldIncrease.setEditable(false);
        fieldIncrease.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        javax.swing.GroupLayout panelWeaponsLayout = new javax.swing.GroupLayout(panelWeapons);
        panelWeapons.setLayout(panelWeaponsLayout);
        panelWeaponsLayout.setHorizontalGroup(
            panelWeaponsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollWeapons, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
            .addGroup(panelWeaponsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelUpgradesWeap, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboUpgradesWeapons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelInflation, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldInflation, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelIncrease, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldIncrease, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelWeaponsLayout.setVerticalGroup(
            panelWeaponsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelWeaponsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelWeaponsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelWeaponsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelIncrease)
                        .addComponent(fieldIncrease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelWeaponsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelUpgradesWeap)
                        .addComponent(comboUpgradesWeapons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelInflation)
                        .addComponent(fieldInflation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollWeapons, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE))
        );

        checkBuilding.setText("Is a building");

        javax.swing.GroupLayout tabUnitsLayout = new javax.swing.GroupLayout(tabUnits);
        tabUnits.setLayout(tabUnitsLayout);
        tabUnitsLayout.setHorizontalGroup(
            tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnitsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelStats, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTerrain, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabUnitsLayout.createSequentialGroup()
                        .addComponent(labelSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboSize, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelParts)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboParts, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelMovement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboMove, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkBuilding, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabUnitsLayout.createSequentialGroup()
                        .addComponent(labelEssential)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldEssential, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelCapture)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboCapture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelRepair)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldRepair, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabUnitsLayout.createSequentialGroup()
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(tabUnitsLayout.createSequentialGroup()
                                .addComponent(labelUnit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(comboUnits, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabUnitsLayout.createSequentialGroup()
                                .addComponent(labelSeries)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboSeries, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelReward)
                            .addComponent(labelSell))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fieldReward, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fieldSell, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelAbilities, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tabUnitsLayout.createSequentialGroup()
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelMisc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelFamilyUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelUnknown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelWeapons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        tabUnitsLayout.setVerticalGroup(
            tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabUnitsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(tabUnitsLayout.createSequentialGroup()
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelUnit)
                            .addComponent(comboUnits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelReward)
                            .addComponent(fieldReward, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelSeries)
                            .addComponent(comboSeries, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelSell)
                            .addComponent(fieldSell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelEssential)
                            .addComponent(fieldEssential, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelCapture)
                            .addComponent(comboCapture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelRepair)
                                .addComponent(fieldRepair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelSize)
                            .addComponent(comboSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelParts)
                            .addComponent(comboParts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelMovement)
                            .addComponent(comboMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkBuilding))
                        .addGap(8, 8, 8)
                        .addComponent(panelTerrain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelStats, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelAbilities, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(tabUnitsLayout.createSequentialGroup()
                        .addGroup(tabUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelUnknown, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabUnitsLayout.createSequentialGroup()
                                .addComponent(panelFamilyUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelMisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelWeapons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabsPanel.addTab("Units & Weapons", tabUnits);

        tabCharacters.setPreferredSize(new java.awt.Dimension(1047, 700));

        labelCharacter.setText("Character:");

        labelSeriesChar.setText("Robot series:");

        comboChars.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));
        comboChars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCharsActionPerformed(evt);
            }
        });

        comboSeriesChar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        labelPersonality.setText("Personality:");

        labelLibIDChar.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelLibIDChar.setText("Library ID:");

        comboPersonality.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00 Normal", "01 Super strong", "02 Strong", "03 Cool", "04 Cautious", "05 Small fry", "06 Mid-boss", "07 Boss", "08 Building", "09 Subpilot", "10 NPC" }));
        comboPersonality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPersonalityActionPerformed(evt);
            }
        });

        fieldLibIDChar.setEditable(false);
        fieldLibIDChar.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLibIDChar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLibIDCharKeyReleased(evt);
            }
        });

        labelAlly.setText("Ally / Enemy:");

        comboAlly.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 -", "1 Ally", "2 Enemy", "3 Both" }));

        labelSkillParts.setText("Skill parts:");

        comboSkillParts.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4" }));

        labelEnemyAI.setText("Enemy AI (?):");
        labelEnemyAI.setToolTipText("");

        fieldEnemyAI.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldEnemyAI.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldEnemyAIKeyReleased(evt);
            }
        });

        labelPortrait.setText("Portrait / Battle lines ID:");

        fieldPortrait.setEditable(false);
        fieldPortrait.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPortrait.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPortraitKeyReleased(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        panelFamilyChar.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pilot family", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 102, 255))); // NOI18N

        checkFamChar01.setText("Gundam");

        checkFamChar02.setText("L-Gaim");

        checkFamChar03.setText("Layzner");

        checkFamChar04.setText("Dragonar");

        checkFamChar05.setText("Mazinger");

        checkFamChar06.setText("Getter");

        checkFamChar07.setText("RaijinOh");

        checkFamChar08.setText("Eiji");

        checkFamChar09.setText("Kaine");

        checkFamChar10.setText("Tapp");

        checkFamChar11.setText("Light");

        checkFamChar12.setText("Kouji (Z)");

        checkFamChar13.setText("Lilith (sub)");

        checkFamChar14.setText("Unused #1");

        checkFamChar15.setText("Unused #2");

        checkFamChar16.setText("Unused #3");

        javax.swing.GroupLayout panelFamilyCharLayout = new javax.swing.GroupLayout(panelFamilyChar);
        panelFamilyChar.setLayout(panelFamilyCharLayout);
        panelFamilyCharLayout.setHorizontalGroup(
            panelFamilyCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFamilyCharLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFamilyCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFamilyCharLayout.createSequentialGroup()
                        .addComponent(checkFamChar01, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar05, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar09, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFamilyCharLayout.createSequentialGroup()
                        .addComponent(checkFamChar02, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar06, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar14, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFamilyCharLayout.createSequentialGroup()
                        .addComponent(checkFamChar03, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar07, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar15, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFamilyCharLayout.createSequentialGroup()
                        .addComponent(checkFamChar04, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar08, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar12, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkFamChar16, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFamilyCharLayout.setVerticalGroup(
            panelFamilyCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFamilyCharLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFamilyCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamChar01)
                    .addComponent(checkFamChar05)
                    .addComponent(checkFamChar09)
                    .addComponent(checkFamChar13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFamilyCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamChar02)
                    .addComponent(checkFamChar06)
                    .addComponent(checkFamChar10)
                    .addComponent(checkFamChar14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFamilyCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamChar03)
                    .addComponent(checkFamChar07)
                    .addComponent(checkFamChar11)
                    .addComponent(checkFamChar15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFamilyCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFamChar04)
                    .addComponent(checkFamChar08)
                    .addComponent(checkFamChar12)
                    .addComponent(checkFamChar16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelGrowthSchema.setText("Stat growth:");

        comboGrowthSchema.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 Mediocre", "1 Melee", "2 Melee + Defense", "3 Melee + Skill", "4 Ranged", "5 Ranged + Defense", "6 Range + Skill", "7 Well rounded", "8 NPC" }));
        comboGrowthSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboGrowthSchemaActionPerformed(evt);
            }
        });

        panelStatsChar.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stats", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 109, 255))); // NOI18N

        labelHeader1.setText("                   Base       Max                              Base       Max                             Base       Max        ");

        fieldMeleeBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldMeleeBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldMeleeBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldMeleeBaseKeyTyped(evt);
            }
        });

        labelMelee.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelMelee.setText("Melee");

        fieldMeleeMax.setEditable(false);
        fieldMeleeMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelMeleeRank.setText("B");

        labelDefense.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDefense.setText("Defense");

        fieldDefenseBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldDefenseBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldDefenseBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldDefenseBaseKeyTyped(evt);
            }
        });

        fieldDefenseMax.setEditable(false);
        fieldDefenseMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelDefenseRank.setText("B");

        fieldAccuracyBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldAccuracyBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldAccuracyBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldAccuracyBaseKeyTyped(evt);
            }
        });

        labelAccuracyRank.setText("S");

        fieldAccuracyMax.setEditable(false);
        fieldAccuracyMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelAccuracy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelAccuracy.setText("Accuracy");

        labelRanged.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelRanged.setText("Ranged");

        fieldRangedBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldRangedBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldRangedBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldRangedBaseKeyTyped(evt);
            }
        });

        fieldRangedMax.setEditable(false);
        fieldRangedMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelRangedRank.setText("B");

        labelSkill.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSkill.setText("Skill");

        fieldSkillBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldSkillBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSkillBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldSkillBaseKeyTyped(evt);
            }
        });

        fieldSkillMax.setEditable(false);
        fieldSkillMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelSkillRank.setText("B");

        labelEvasion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelEvasion.setText("Evasion");

        fieldEvasionBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldEvasionBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldEvasionBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldEvasionBaseKeyTyped(evt);
            }
        });

        fieldEvasionMax.setEditable(false);
        fieldEvasionMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelEvasionRank.setText("B");

        javax.swing.GroupLayout panelStatsCharLayout = new javax.swing.GroupLayout(panelStatsChar);
        panelStatsChar.setLayout(panelStatsCharLayout);
        panelStatsCharLayout.setHorizontalGroup(
            panelStatsCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatsCharLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStatsCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelHeader1, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelStatsCharLayout.createSequentialGroup()
                        .addComponent(labelMelee, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldMeleeBase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(fieldMeleeMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelMeleeRank, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelDefense, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldDefenseBase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(fieldDefenseMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelDefenseRank, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelAccuracy, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldAccuracyBase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(fieldAccuracyMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAccuracyRank, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelStatsCharLayout.createSequentialGroup()
                        .addComponent(labelRanged, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldRangedBase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(fieldRangedMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelRangedRank, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelSkill, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldSkillBase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(fieldSkillMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelSkillRank, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelEvasion, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldEvasionBase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(fieldEvasionMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelEvasionRank, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelStatsCharLayout.setVerticalGroup(
            panelStatsCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStatsCharLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHeader1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatsCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMelee)
                    .addComponent(fieldMeleeBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldMeleeMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMeleeRank)
                    .addComponent(labelDefense)
                    .addComponent(fieldDefenseBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldDefenseMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelDefenseRank)
                    .addComponent(labelAccuracy)
                    .addComponent(fieldAccuracyBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldAccuracyMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelAccuracyRank))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelStatsCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelRanged)
                    .addComponent(fieldRangedBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldRangedMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelRangedRank)
                    .addComponent(labelSkill)
                    .addComponent(fieldSkillBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldSkillMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSkillRank)
                    .addComponent(labelEvasion)
                    .addComponent(fieldEvasionBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldEvasionMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelEvasionRank))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSkillsLevel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Skill levels", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 109, 255))); // NOI18N

        labelHeader2.setText("                        Lv1        Lv2        Lv3        Lv4        Lv5        Lv6        Lv7        Lv8        Lv9");

        labelNTlevel.setText("(Cyber) NT");

        fieldNTlv1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv1KeyReleased(evt);
            }
        });

        fieldNTlv2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv2KeyReleased(evt);
            }
        });

        fieldNTlv3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv3KeyReleased(evt);
            }
        });

        fieldNTlv4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv4KeyReleased(evt);
            }
        });

        fieldNTlv5.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv5KeyReleased(evt);
            }
        });

        fieldNTlv6.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv6KeyReleased(evt);
            }
        });

        fieldNTlv7.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv7KeyReleased(evt);
            }
        });

        fieldNTlv8.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv8KeyReleased(evt);
            }
        });

        fieldNTlv9.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldNTlv9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldNTlv9KeyReleased(evt);
            }
        });

        labelPotentialLevel.setText("Potential");

        fieldPotentialLv1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv1KeyReleased(evt);
            }
        });

        fieldPotentialLv2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv2KeyReleased(evt);
            }
        });

        fieldPotentialLv3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv3KeyReleased(evt);
            }
        });

        fieldPotentialLv4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv4KeyReleased(evt);
            }
        });

        fieldPotentialLv5.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv5KeyReleased(evt);
            }
        });

        fieldPotentialLv6.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv6KeyReleased(evt);
            }
        });

        fieldPotentialLv7.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv7KeyReleased(evt);
            }
        });

        fieldPotentialLv8.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv8KeyReleased(evt);
            }
        });

        fieldPotentialLv9.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPotentialLv9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldPotentialLv9KeyReleased(evt);
            }
        });

        labelSupportLevel.setText("Support");

        fieldSupportLv1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldSupportLv1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSupportLv1KeyReleased(evt);
            }
        });

        fieldSupportLv2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldSupportLv2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSupportLv2KeyReleased(evt);
            }
        });

        fieldSupportLv3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldSupportLv3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSupportLv3KeyReleased(evt);
            }
        });

        fieldSupportLv4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldSupportLv4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSupportLv4KeyReleased(evt);
            }
        });

        labelCommandLevel.setText("Command");

        fieldCommandLv1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCommandLv1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCommandLv1KeyReleased(evt);
            }
        });

        fieldCommandLv2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCommandLv2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCommandLv2KeyReleased(evt);
            }
        });

        fieldCommandLv3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCommandLv3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCommandLv3KeyReleased(evt);
            }
        });

        fieldCommandLv4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCommandLv4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCommandLv4KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelSkillsLevelLayout = new javax.swing.GroupLayout(panelSkillsLevel);
        panelSkillsLevel.setLayout(panelSkillsLevelLayout);
        panelSkillsLevelLayout.setHorizontalGroup(
            panelSkillsLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSkillsLevelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSkillsLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelHeader2, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelSkillsLevelLayout.createSequentialGroup()
                        .addComponent(labelNTlevel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldNTlv1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldNTlv9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSkillsLevelLayout.createSequentialGroup()
                        .addComponent(labelPotentialLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPotentialLv1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldPotentialLv9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSkillsLevelLayout.createSequentialGroup()
                        .addComponent(labelSupportLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldSupportLv1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldSupportLv2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldSupportLv3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldSupportLv4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSkillsLevelLayout.createSequentialGroup()
                        .addComponent(labelCommandLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldCommandLv1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldCommandLv2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldCommandLv3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldCommandLv4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSkillsLevelLayout.setVerticalGroup(
            panelSkillsLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSkillsLevelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHeader2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSkillsLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelNTlevel)
                    .addComponent(fieldNTlv1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldNTlv9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSkillsLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPotentialLevel)
                    .addComponent(fieldPotentialLv1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPotentialLv9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSkillsLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSupportLevel)
                    .addComponent(fieldSupportLv1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldSupportLv2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldSupportLv3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldSupportLv4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSkillsLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCommandLevel)
                    .addComponent(fieldCommandLv1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCommandLv2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCommandLv3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCommandLv4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSkills.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Skills list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 109, 255))); // NOI18N

        checkSkill01.setText("NOT a Cyber NT");

        checkSkill02.setText("NOT a Newtype");

        checkSkill03.setText("Potential");

        checkSkill04.setText("Shield Defense");

        checkSkill05.setText("Support ATK");

        checkSkill06.setText("Support DEF");

        checkSkill07.setText("Command");

        checkSkill08.setText("Instinct");

        checkSkill09.setText("Counter");

        checkSkill10.setText("Hit & Away");

        checkSkill11.setText("Sniping");

        checkSkill12.setText("???");
        checkSkill12.setEnabled(false);

        checkSkill13.setText("???");
        checkSkill13.setEnabled(false);

        checkSkill14.setText("???");
        checkSkill14.setEnabled(false);

        checkSkill15.setText("???");
        checkSkill15.setEnabled(false);

        checkSkill16.setText("???");
        checkSkill16.setEnabled(false);

        javax.swing.GroupLayout panelSkillsLayout = new javax.swing.GroupLayout(panelSkills);
        panelSkills.setLayout(panelSkillsLayout);
        panelSkillsLayout.setHorizontalGroup(
            panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSkillsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkSkill01, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSkill04, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSkill02, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSkill03, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkSkill05, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSkill08, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSkill06, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkSkill07, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSkillsLayout.createSequentialGroup()
                        .addComponent(checkSkill11, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkSkill15, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSkillsLayout.createSequentialGroup()
                        .addComponent(checkSkill10, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkSkill14, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSkillsLayout.createSequentialGroup()
                        .addComponent(checkSkill09, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkSkill13, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelSkillsLayout.createSequentialGroup()
                        .addComponent(checkSkill12, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkSkill16, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSkillsLayout.setVerticalGroup(
            panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSkillsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkSkill01)
                    .addComponent(checkSkill05)
                    .addComponent(checkSkill09)
                    .addComponent(checkSkill13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkSkill02)
                    .addComponent(checkSkill06)
                    .addComponent(checkSkill10)
                    .addComponent(checkSkill14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkSkill03)
                    .addComponent(checkSkill07)
                    .addComponent(checkSkill11)
                    .addComponent(checkSkill15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSkillsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkSkill04)
                    .addComponent(checkSkill08)
                    .addComponent(checkSkill12)
                    .addComponent(checkSkill16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSpirit.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Spirit commands", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 109, 255))); // NOI18N

        labelHeader3.setText("                   Base       Max");

        labelSP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSP.setText("SP");

        fieldSPBase.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldSPBase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldSPBaseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fieldSPBaseKeyTyped(evt);
            }
        });

        fieldSPMax.setEditable(false);
        fieldSPMax.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelSPRank.setText("B");

        labelHeader4.setText("                   Command                      SP cost      @ Lv.");

        labelCommand1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCommand1.setText("#1");

        comboCommand1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        fieldCostCommand1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCostCommand1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCostCommand1KeyReleased(evt);
            }
        });

        fieldLearnCommand1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLearnCommand1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLearnCommand1KeyReleased(evt);
            }
        });

        labelCommand2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCommand2.setText("#2");

        comboCommand2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        fieldCostCommand2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCostCommand2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCostCommand2KeyReleased(evt);
            }
        });

        fieldLearnCommand2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLearnCommand2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLearnCommand2KeyReleased(evt);
            }
        });

        labelCommand3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCommand3.setText("#3");

        comboCommand3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        fieldCostCommand3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCostCommand3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCostCommand3KeyReleased(evt);
            }
        });

        fieldLearnCommand3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLearnCommand3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLearnCommand3KeyReleased(evt);
            }
        });

        labelCommand4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCommand4.setText("#4");

        comboCommand4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        fieldCostCommand4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCostCommand4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCostCommand4KeyReleased(evt);
            }
        });

        fieldLearnCommand4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLearnCommand4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLearnCommand4KeyReleased(evt);
            }
        });

        labelCommand5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCommand5.setText("#5");

        comboCommand5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        fieldCostCommand5.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCostCommand5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCostCommand5KeyReleased(evt);
            }
        });

        fieldLearnCommand5.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLearnCommand5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLearnCommand5KeyReleased(evt);
            }
        });

        labelCommand6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCommand6.setText("#6");

        comboCommand6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));

        fieldCostCommand6.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldCostCommand6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldCostCommand6KeyReleased(evt);
            }
        });

        fieldLearnCommand6.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldLearnCommand6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fieldLearnCommand6KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelSpiritLayout = new javax.swing.GroupLayout(panelSpirit);
        panelSpirit.setLayout(panelSpiritLayout);
        panelSpiritLayout.setHorizontalGroup(
            panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSpiritLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(panelSpiritLayout.createSequentialGroup()
                        .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelHeader3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelSpiritLayout.createSequentialGroup()
                                .addComponent(labelSP, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fieldSPBase, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(fieldSPMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelSPRank))
                            .addComponent(labelHeader4, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelSpiritLayout.createSequentialGroup()
                                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelSpiritLayout.createSequentialGroup()
                                        .addComponent(labelCommand1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboCommand1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelSpiritLayout.createSequentialGroup()
                                        .addComponent(labelCommand2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboCommand2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelSpiritLayout.createSequentialGroup()
                                        .addComponent(labelCommand3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboCommand3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelSpiritLayout.createSequentialGroup()
                                        .addComponent(labelCommand4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboCommand4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelSpiritLayout.createSequentialGroup()
                                        .addComponent(labelCommand5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboCommand5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelSpiritLayout.createSequentialGroup()
                                        .addComponent(labelCommand6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboCommand6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fieldCostCommand1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldCostCommand2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldCostCommand3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldCostCommand4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldCostCommand5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldCostCommand6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fieldLearnCommand1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLearnCommand2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLearnCommand3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLearnCommand4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLearnCommand5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLearnCommand6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelSpiritLayout.setVerticalGroup(
            panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSpiritLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHeader3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSP)
                    .addComponent(fieldSPBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldSPMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSPRank))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelHeader4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCommand1)
                    .addComponent(comboCommand1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCostCommand1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldLearnCommand1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCommand2)
                    .addComponent(comboCommand2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCostCommand2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldLearnCommand2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCommand3)
                    .addComponent(comboCommand3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCostCommand3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldLearnCommand3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCommand4)
                    .addComponent(comboCommand4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCostCommand4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldLearnCommand4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCommand5)
                    .addComponent(comboCommand5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCostCommand5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldLearnCommand5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpiritLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCommand6)
                    .addComponent(comboCommand6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCostCommand6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldLearnCommand6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        panelUnknownChar.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Unknown Bytes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 0, 51))); // NOI18N

        labelByteChar70.setText("#70");
        labelByteChar70.setToolTipText("");

        fieldByteChar70.setEditable(false);
        fieldByteChar70.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByteChar71.setText("#71");
        labelByteChar71.setToolTipText("");

        fieldByteChar71.setEditable(false);
        fieldByteChar71.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByteChar72.setText("#72");
        labelByteChar72.setToolTipText("\n");

        fieldByteChar72.setEditable(false);
        fieldByteChar72.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        fieldByteChar84.setEditable(false);
        fieldByteChar84.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        fieldByteChar81.setEditable(false);
        fieldByteChar81.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByteChar81.setText("#81");
        labelByteChar81.setToolTipText("");

        labelByteChar82.setText("#82");
        labelByteChar82.setToolTipText("");

        fieldByteChar80.setEditable(false);
        fieldByteChar80.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByteChar79.setText("#79");
        labelByteChar79.setToolTipText("");

        labelByteChar80.setText("#80");
        labelByteChar80.setToolTipText("");

        fieldByteChar83.setEditable(false);
        fieldByteChar83.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        fieldByteChar82.setEditable(false);
        fieldByteChar82.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        labelByteChar83.setText("#83");
        labelByteChar83.setToolTipText("Always 0");

        labelByteChar84.setText("#84");
        labelByteChar84.setToolTipText("Always 0");

        fieldByteChar79.setEditable(false);
        fieldByteChar79.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        javax.swing.GroupLayout panelUnknownCharLayout = new javax.swing.GroupLayout(panelUnknownChar);
        panelUnknownChar.setLayout(panelUnknownCharLayout);
        panelUnknownCharLayout.setHorizontalGroup(
            panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUnknownCharLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUnknownCharLayout.createSequentialGroup()
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownCharLayout.createSequentialGroup()
                                .addComponent(labelByteChar79, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldByteChar79, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownCharLayout.createSequentialGroup()
                                .addComponent(labelByteChar80, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldByteChar80, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelUnknownCharLayout.createSequentialGroup()
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelUnknownCharLayout.createSequentialGroup()
                                .addComponent(labelByteChar70, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldByteChar70, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownCharLayout.createSequentialGroup()
                                    .addComponent(labelByteChar71, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldByteChar71, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownCharLayout.createSequentialGroup()
                                    .addComponent(labelByteChar72, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldByteChar72, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelUnknownCharLayout.createSequentialGroup()
                                .addComponent(labelByteChar81, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldByteChar81, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelUnknownCharLayout.createSequentialGroup()
                                .addComponent(labelByteChar82, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldByteChar82, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownCharLayout.createSequentialGroup()
                                    .addComponent(labelByteChar83, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldByteChar83, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUnknownCharLayout.createSequentialGroup()
                                    .addComponent(labelByteChar84, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldByteChar84, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        panelUnknownCharLayout.setVerticalGroup(
            panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUnknownCharLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUnknownCharLayout.createSequentialGroup()
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByteChar70)
                            .addComponent(fieldByteChar70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByteChar71)
                            .addComponent(fieldByteChar71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByteChar72)
                            .addComponent(fieldByteChar72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelUnknownCharLayout.createSequentialGroup()
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByteChar81)
                            .addComponent(fieldByteChar81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByteChar82)
                            .addComponent(fieldByteChar82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByteChar83)
                            .addComponent(fieldByteChar83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelByteChar84)
                            .addComponent(fieldByteChar84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelByteChar79)
                    .addComponent(fieldByteChar79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelUnknownCharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelByteChar80)
                    .addComponent(fieldByteChar80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPersonality.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Personality will modifiers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(5, 109, 255))); // NOI18N

        labelHit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelHit.setText("Hit target");

        fieldPersHit.setEditable(false);
        fieldPersHit.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPersHit.setText("+0");

        labelMiss.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelMiss.setText("Miss attack");

        fieldPersMiss.setEditable(false);
        fieldPersMiss.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPersMiss.setText("+0");

        labelEvade.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelEvade.setText("Evade");

        fieldPersEvade.setEditable(false);
        fieldPersEvade.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPersEvade.setText("+0");

        labelDamage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDamage.setText("Damage taken");

        fieldPersDamage.setEditable(false);
        fieldPersDamage.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPersDamage.setText("+1");

        labelEnemyKill.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelEnemyKill.setText("Enemy shot down");

        fieldPersEnemy.setEditable(false);
        fieldPersEnemy.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPersEnemy.setText("+3");

        labelAllyKill.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelAllyKill.setText("Ally shot down");

        fieldPersAlly.setEditable(false);
        fieldPersAlly.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fieldPersAlly.setText("+0");

        javax.swing.GroupLayout panelPersonalityLayout = new javax.swing.GroupLayout(panelPersonality);
        panelPersonality.setLayout(panelPersonalityLayout);
        panelPersonalityLayout.setHorizontalGroup(
            panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPersonalityLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPersonalityLayout.createSequentialGroup()
                        .addComponent(labelHit, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPersHit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelDamage, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPersDamage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPersonalityLayout.createSequentialGroup()
                        .addComponent(labelMiss, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPersMiss, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelEnemyKill, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPersEnemy, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPersonalityLayout.createSequentialGroup()
                        .addComponent(labelEvade, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPersEvade, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelAllyKill, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPersAlly, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPersonalityLayout.setVerticalGroup(
            panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPersonalityLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelDamage)
                        .addComponent(fieldPersDamage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelHit)
                        .addComponent(fieldPersHit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelEnemyKill)
                        .addComponent(fieldPersEnemy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelMiss)
                        .addComponent(fieldPersMiss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelAllyKill)
                        .addComponent(fieldPersAlly, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPersonalityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelEvade)
                        .addComponent(fieldPersEvade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelSkillAces.setText("Skill Aces:");

        comboSkillAces.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 No attack aces", "1 Melee", "2 Ranged", "3 Melee + Ranged" }));

        javax.swing.GroupLayout tabCharactersLayout = new javax.swing.GroupLayout(tabCharacters);
        tabCharacters.setLayout(tabCharactersLayout);
        tabCharactersLayout.setHorizontalGroup(
            tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCharactersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabCharactersLayout.createSequentialGroup()
                        .addComponent(labelAlly, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboAlly, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labelEnemyAI, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldEnemyAI, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelPortrait, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldPortrait, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabCharactersLayout.createSequentialGroup()
                        .addComponent(labelSkillParts, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboSkillParts, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelSkillAces, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboSkillAces, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelGrowthSchema, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboGrowthSchema, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelStatsChar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSkillsLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tabCharactersLayout.createSequentialGroup()
                        .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(tabCharactersLayout.createSequentialGroup()
                                .addComponent(labelCharacter, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(comboChars, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabCharactersLayout.createSequentialGroup()
                                .addComponent(labelSeriesChar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboSeriesChar, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabCharactersLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelLibIDChar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldLibIDChar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabCharactersLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(labelPersonality, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboPersonality, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(panelSkills, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabCharactersLayout.createSequentialGroup()
                        .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelPersonality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelSpirit, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelUnknownChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelFamilyChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(188, 188, 188))
        );
        tabCharactersLayout.setVerticalGroup(
            tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCharactersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabCharactersLayout.createSequentialGroup()
                        .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2)
                            .addGroup(tabCharactersLayout.createSequentialGroup()
                                .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelCharacter)
                                    .addComponent(comboChars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelPersonality)
                                    .addComponent(comboPersonality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelSeriesChar)
                                    .addComponent(comboSeriesChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelLibIDChar)
                                    .addComponent(fieldLibIDChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelEnemyAI)
                                        .addComponent(fieldEnemyAI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelAlly)
                                        .addComponent(comboAlly, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelPortrait)
                                        .addComponent(fieldPortrait, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelSkillParts)
                                    .addComponent(comboSkillParts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelGrowthSchema)
                                    .addComponent(comboGrowthSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelSkillAces)
                                    .addComponent(comboSkillAces, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panelStatsChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelSkillsLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelSkills, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(tabCharactersLayout.createSequentialGroup()
                        .addGroup(tabCharactersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabCharactersLayout.createSequentialGroup()
                                .addComponent(panelPersonality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelSpirit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panelUnknownChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelFamilyChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(130, 130, 130))))
        );

        tabsPanel.addTab("Characters", tabCharacters);

        menuFile.setText("File");

        itemOpenBin.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemOpenBin.setText("Load add02dat.bin");
        itemOpenBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemOpenBinActionPerformed(evt);
            }
        });
        menuFile.add(itemOpenBin);
        menuFile.add(jSeparator4);

        menuExport.setText("Export to TXT...");
        menuExport.setEnabled(false);

        itemUnitsExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemUnitsExport.setText("Units");
        itemUnitsExport.setEnabled(false);
        itemUnitsExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemUnitsExportActionPerformed(evt);
            }
        });
        menuExport.add(itemUnitsExport);

        itemWeaponsExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemWeaponsExport.setText("Weapons");
        itemWeaponsExport.setEnabled(false);
        itemWeaponsExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemWeaponsExportActionPerformed(evt);
            }
        });
        menuExport.add(itemWeaponsExport);

        itemCharactersExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemCharactersExport.setText("Characters");
        itemCharactersExport.setEnabled(false);
        itemCharactersExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCharactersExportActionPerformed(evt);
            }
        });
        menuExport.add(itemCharactersExport);

        menuFile.add(menuExport);

        menuImport.setText("Import from TXT...");
        menuImport.setEnabled(false);

        itemUnitsImport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemUnitsImport.setText("Units");
        itemUnitsImport.setEnabled(false);
        itemUnitsImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemUnitsImportActionPerformed(evt);
            }
        });
        menuImport.add(itemUnitsImport);

        itemWeaponsImport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemWeaponsImport.setText("Weapons");
        itemWeaponsImport.setEnabled(false);
        itemWeaponsImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemWeaponsImportActionPerformed(evt);
            }
        });
        menuImport.add(itemWeaponsImport);

        itemCharactersImport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemCharactersImport.setText("Characters");
        itemCharactersImport.setEnabled(false);
        itemCharactersImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCharactersImportActionPerformed(evt);
            }
        });
        menuImport.add(itemCharactersImport);

        menuFile.add(menuImport);
        menuFile.add(jSeparator6);

        itemSaveBin.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemSaveBin.setText("Save add02dat.bin");
        itemSaveBin.setEnabled(false);
        itemSaveBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSaveBinActionPerformed(evt);
            }
        });
        menuFile.add(itemSaveBin);
        menuFile.add(jSeparator5);

        itemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemExit.setText("Exit");
        itemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemExitActionPerformed(evt);
            }
        });
        menuFile.add(itemExit);

        menuBar.add(menuFile);

        menuEdit.setText("Edit");

        checkItemSafety.setSelected(true);
        checkItemSafety.setText("Safety lock");
        checkItemSafety.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkItemSafetyActionPerformed(evt);
            }
        });
        menuEdit.add(checkItemSafety);

        menuBar.add(menuEdit);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboBodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBodyActionPerformed
        setBody();
    }//GEN-LAST:event_comboBodyActionPerformed

    private void comboHeadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboHeadActionPerformed
        setHead();
    }//GEN-LAST:event_comboHeadActionPerformed

    private void comboArmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboArmsActionPerformed
        setArms();
    }//GEN-LAST:event_comboArmsActionPerformed

    private void comboLegsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboLegsActionPerformed
        setLegs();
    }//GEN-LAST:event_comboLegsActionPerformed

    private void fieldBodyBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldBodyBaseKeyReleased
        setBodyMax();
    }//GEN-LAST:event_fieldBodyBaseKeyReleased

    private void fieldBodyBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldBodyBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldBodyBaseKeyTyped

    private void fieldHeadBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldHeadBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldHeadBaseKeyTyped

    private void fieldArmsBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldArmsBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldArmsBaseKeyTyped

    private void fieldLegsBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLegsBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldLegsBaseKeyTyped

    private void fieldEnergyBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldEnergyBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldEnergyBaseKeyTyped

    private void fieldMobilityBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldMobilityBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldMobilityBaseKeyTyped

    private void fieldArmorBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldArmorBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldArmorBaseKeyTyped

    private void fieldHeadBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldHeadBaseKeyReleased
        setHeadMax();
    }//GEN-LAST:event_fieldHeadBaseKeyReleased

    private void fieldArmsBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldArmsBaseKeyReleased
        setArmsMax();
    }//GEN-LAST:event_fieldArmsBaseKeyReleased

    private void fieldLegsBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLegsBaseKeyReleased
        setLegsMax();
    }//GEN-LAST:event_fieldLegsBaseKeyReleased

    private void fieldEnergyBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldEnergyBaseKeyReleased
        setEnergyMax();
    }//GEN-LAST:event_fieldEnergyBaseKeyReleased

    private void fieldMobilityBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldMobilityBaseKeyReleased
        setMobilityMax();
    }//GEN-LAST:event_fieldMobilityBaseKeyReleased

    private void fieldArmorBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldArmorBaseKeyReleased
        setArmorMax();
    }//GEN-LAST:event_fieldArmorBaseKeyReleased

    private void comboUpgradesHPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUpgradesHPActionPerformed
        setBodyMax();
        setHeadMax();
        setArmsMax();
        setLegsMax();
    }//GEN-LAST:event_comboUpgradesHPActionPerformed

    private void comboUpgradesENActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUpgradesENActionPerformed
        setEnergyMax();
    }//GEN-LAST:event_comboUpgradesENActionPerformed

    private void comboUpgradesMobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUpgradesMobActionPerformed
        setMobilityMax();
    }//GEN-LAST:event_comboUpgradesMobActionPerformed

    private void comboUpgradesArmorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUpgradesArmorActionPerformed
        setArmorMax();
    }//GEN-LAST:event_comboUpgradesArmorActionPerformed

    private void fieldRewardKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldRewardKeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
        setSellValue();
    }//GEN-LAST:event_fieldRewardKeyReleased

    private void fieldRewardKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldRewardKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldRewardKeyTyped

    private void comboUpgradesWeaponsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUpgradesWeaponsActionPerformed
        setUpgradesWeapons();
    }//GEN-LAST:event_comboUpgradesWeaponsActionPerformed

    private void fieldInflationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldInflationKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
        setInflIncrease();
    }//GEN-LAST:event_fieldInflationKeyReleased

    private void fieldInflationKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldInflationKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldInflationKeyTyped

    private void fieldEssentialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldEssentialKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldEssentialKeyTyped

    private void fieldRepairKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldRepairKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldRepairKeyTyped

    private void fieldAIKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldAIKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldAIKeyTyped

    private void comboPersonalityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPersonalityActionPerformed
        setPersonalityInfo();
        setMaxStats();
    }//GEN-LAST:event_comboPersonalityActionPerformed

    private void comboGrowthSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboGrowthSchemaActionPerformed
        setMaxStats();
    }//GEN-LAST:event_comboGrowthSchemaActionPerformed

    private void fieldMeleeBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldMeleeBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldMeleeBaseKeyTyped

    private void fieldRangedBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldRangedBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldRangedBaseKeyTyped

    private void fieldDefenseBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldDefenseBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldDefenseBaseKeyTyped

    private void fieldSkillBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSkillBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldSkillBaseKeyTyped

    private void fieldAccuracyBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldAccuracyBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldAccuracyBaseKeyTyped

    private void fieldEvasionBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldEvasionBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldEvasionBaseKeyTyped

    private void fieldSPBaseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSPBaseKeyTyped
        filterNonNumber(evt);
    }//GEN-LAST:event_fieldSPBaseKeyTyped

    private void fieldMeleeBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldMeleeBaseKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
        setMeleeMax();
    }//GEN-LAST:event_fieldMeleeBaseKeyReleased

    private void fieldRangedBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldRangedBaseKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
        setRangedMax();
    }//GEN-LAST:event_fieldRangedBaseKeyReleased

    private void fieldDefenseBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldDefenseBaseKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
        setDefenseMax();
    }//GEN-LAST:event_fieldDefenseBaseKeyReleased

    private void fieldSkillBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSkillBaseKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
        setSkillMax();
    }//GEN-LAST:event_fieldSkillBaseKeyReleased

    private void fieldAccuracyBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldAccuracyBaseKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
        setAccuracyMax();
    }//GEN-LAST:event_fieldAccuracyBaseKeyReleased

    private void fieldEvasionBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldEvasionBaseKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
        setEvasionMax();
    }//GEN-LAST:event_fieldEvasionBaseKeyReleased

    private void fieldSPBaseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSPBaseKeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
        setSPMax();
    }//GEN-LAST:event_fieldSPBaseKeyReleased

    private void itemOpenBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemOpenBinActionPerformed
        openBinFile();
    }//GEN-LAST:event_itemOpenBinActionPerformed

    private void comboCharsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCharsActionPerformed
        saveChar();
        
        current_char = comboChars.getSelectedIndex();
        
        loadChar();
    }//GEN-LAST:event_comboCharsActionPerformed

    private void comboUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUnitsActionPerformed
        saveUnit();
        
        current_unit = comboUnits.getSelectedIndex();
        
        loadUnit();
        applySafety();
    }//GEN-LAST:event_comboUnitsActionPerformed

    private void itemSaveBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSaveBinActionPerformed
        saveBinFile();
    }//GEN-LAST:event_itemSaveBinActionPerformed

    private void itemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExitActionPerformed
        this.dispose();
    }//GEN-LAST:event_itemExitActionPerformed

    private void checkItemSafetyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkItemSafetyActionPerformed
        applySafety();
    }//GEN-LAST:event_checkItemSafetyActionPerformed

    private void itemUnitsExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemUnitsExportActionPerformed
        exportUnitsCSV();
    }//GEN-LAST:event_itemUnitsExportActionPerformed

    private void itemUnitsImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemUnitsImportActionPerformed
        importUnitsCSV();
    }//GEN-LAST:event_itemUnitsImportActionPerformed

    private void itemWeaponsExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemWeaponsExportActionPerformed
        exportWeaponsCSV();
    }//GEN-LAST:event_itemWeaponsExportActionPerformed

    private void itemCharactersExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCharactersExportActionPerformed
        exportCharactersCSV();
    }//GEN-LAST:event_itemCharactersExportActionPerformed

    private void itemWeaponsImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemWeaponsImportActionPerformed
        importWeaponsCSV();
    }//GEN-LAST:event_itemWeaponsImportActionPerformed

    private void itemCharactersImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCharactersImportActionPerformed
        importCharactersCSV();
    }//GEN-LAST:event_itemCharactersImportActionPerformed

    private void fieldRepairKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldRepairKeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldRepairKeyReleased

    private void fieldLibIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLibIDKeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldLibIDKeyReleased

    private void fieldModelIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldModelIDKeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldModelIDKeyReleased

    private void fieldCostCommand1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCostCommand1KeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldCostCommand1KeyReleased

    private void fieldCostCommand2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCostCommand2KeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldCostCommand2KeyReleased

    private void fieldCostCommand3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCostCommand3KeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldCostCommand3KeyReleased

    private void fieldCostCommand4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCostCommand4KeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldCostCommand4KeyReleased

    private void fieldCostCommand5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCostCommand5KeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldCostCommand5KeyReleased

    private void fieldCostCommand6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCostCommand6KeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldCostCommand6KeyReleased

    private void fieldLibIDCharKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLibIDCharKeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldLibIDCharKeyReleased

    private void fieldPortraitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPortraitKeyReleased
        capField( (JTextField) evt.getComponent(), max_short );
    }//GEN-LAST:event_fieldPortraitKeyReleased

    private void fieldEnemyAIKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldEnemyAIKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldEnemyAIKeyReleased

    private void fieldNTlv1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv1KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv1KeyReleased

    private void fieldNTlv2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv2KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv2KeyReleased

    private void fieldNTlv3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv3KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv3KeyReleased

    private void fieldNTlv4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv4KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv4KeyReleased

    private void fieldNTlv5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv5KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv5KeyReleased

    private void fieldNTlv6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv6KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv6KeyReleased

    private void fieldNTlv7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv7KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv7KeyReleased

    private void fieldNTlv8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv8KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv8KeyReleased

    private void fieldNTlv9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldNTlv9KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldNTlv9KeyReleased

    private void fieldPotentialLv1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv1KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv1KeyReleased

    private void fieldPotentialLv2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv2KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv2KeyReleased

    private void fieldPotentialLv3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv3KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv3KeyReleased

    private void fieldPotentialLv4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv4KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv4KeyReleased

    private void fieldPotentialLv5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv5KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv5KeyReleased

    private void fieldPotentialLv6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv6KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv6KeyReleased

    private void fieldPotentialLv7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv7KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv7KeyReleased

    private void fieldPotentialLv8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv8KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv8KeyReleased

    private void fieldPotentialLv9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldPotentialLv9KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldPotentialLv9KeyReleased

    private void fieldSupportLv1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSupportLv1KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldSupportLv1KeyReleased

    private void fieldSupportLv2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSupportLv2KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldSupportLv2KeyReleased

    private void fieldSupportLv3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSupportLv3KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldSupportLv3KeyReleased

    private void fieldSupportLv4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldSupportLv4KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldSupportLv4KeyReleased

    private void fieldCommandLv1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCommandLv1KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldCommandLv1KeyReleased

    private void fieldCommandLv2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCommandLv2KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldCommandLv2KeyReleased

    private void fieldCommandLv3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCommandLv3KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldCommandLv3KeyReleased

    private void fieldCommandLv4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldCommandLv4KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldCommandLv4KeyReleased

    private void fieldLearnCommand1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLearnCommand1KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldLearnCommand1KeyReleased

    private void fieldLearnCommand2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLearnCommand2KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldLearnCommand2KeyReleased

    private void fieldLearnCommand3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLearnCommand3KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldLearnCommand3KeyReleased

    private void fieldLearnCommand4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLearnCommand4KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldLearnCommand4KeyReleased

    private void fieldLearnCommand5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLearnCommand5KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldLearnCommand5KeyReleased

    private void fieldLearnCommand6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldLearnCommand6KeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldLearnCommand6KeyReleased

    private void fieldEssentialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldEssentialKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldEssentialKeyReleased

    private void fieldAIKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fieldAIKeyReleased
        capField( (JTextField) evt.getComponent(), max_byte );
    }//GEN-LAST:event_fieldAIKeyReleased

    
    /*********************************************************/
    /******************* Utilities ***************************/
    /*********************************************************/
        
    private void filterNonNumber(java.awt.event.KeyEvent evt){
        if (evt.getKeyChar() < '0' || evt.getKeyChar() > '9' || evt.getKeyChar() == ' ')
            evt.consume();
    }
    
    // Receives a 4-byte value and returns its int value
    public int parse4bytes(byte[] block){
        int value = 0;

        if (block.length != 4){
            System.err.println("ERROR: Not 4 bytes.");
            return value;
        }

        value = block[0] << 24 | (block[1] & 0xFF) << 16 | (block[2] & 0xFF) << 8 | (block[3] & 0xFF);

        //System.out.println("Value: " + value);

        return value;
    }

    // Receives a 2-byte value and returns its int value
    public int parse2bytes(byte[] block){
        int value = 0;

        if (block.length != 2){
            System.err.println("ERROR: Not 2 bytes.");
            return value;
        }

        value = (block[0] & 0xFF) << 8 | (block[1] & 0xFF);

        return value;
    }

    // Receives an int and return its 4-byte value
    public byte[] int2bytes(int value){
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    // Receives a short and return its 2-byte value
    public byte[] short2bytes(short value){
        return new byte[] {
                (byte)(value >>> 8),
                (byte)value};
    }

    // Return size bytes from b, starting from offset
    // or null, if you asked for something bigger than b
    public byte[] subBlock(byte[] b, int offset, int size){
        byte[] section = null;

        if (offset + size > b.length){
            System.err.println("ERROR: Section is out of block.");
            return section;
        }

        section = new byte[size];

        for (int i = 0; i < size; i++){
            section[i] = b[offset + i];
            //System.out.println("Read: " + (section[i] & 0xff));
        }

        return section;
    }

    // Use this when reading from add02dat.bin
    // The byte array containing the raw SJIS text will be stored in a String, properly formatted
    public String hex2string(byte[] sjis_text){
        String text = "";
        
        try {
            text = new String(sjis_text, "Shift-JIS");
        } catch (UnsupportedEncodingException ex) {
            //System.err.println("Block: " + position + ", Entry: " + pos + " - NOT SJIS!!");
            //Logger.getLogger(TextBlock.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }

    // Finds the last SJIS string in a block b that starts at offset
    // * pre_size is the number of bytes to ignore at the beginning of the block (for library entries)
    //   - Set it to 0 for TextBlocks and UnitForms, 8 for Character Library and 14 for Robot Library
    // * num_separators is the number of separators you want to find before deciding it's a good string
    //   - Set it to 1 for TextBlocks and UnitForms, 4 for Character and Robot Library
    public byte[] findLastString(byte[] b, int offset, int pre_size, int num_separators){
        byte[] last = null;

        int value = -1;
        int counter = 0;

        int i = pre_size;
        boolean found = false;

        for (; offset + i < b.length && !found; i++){
            value = parse2bytes(subBlock(b, offset + i, 2));
            //System.out.println("Value: " + value);
            if (value == 0){ // Separator found
                counter++;
                i++;    // skip 1 byte to go to the next 2-byte group
                if (counter == num_separators){  // Found the last separator
                    //System.out.println("Found.");
                    found = true;   // Exit loop
                }
            }
        }

        last = subBlock(b, offset, i - 2);  // We don't take the separator at the end
        return last;
    }

    // Replaces the conflictive SJIS characters in a block
    // When parsing the file, the following characters are transformed:
    // Ⅰ (87 54) -> Ι (83 a7) * this one is used only in the description of the Deathgrome II
    // Ⅱ (87 55) -> П (84 50)
    // Ⅲ (87 56) -> Ш (84 59)
    // "inverted omega" (eb 3f) -> Ю (84 5f)
    // During reinsertion, the values are reversed
    // This change is necessary because otherwise, the application doesn't recognize
    // the original characters and the data is destroyed during reinsertion
    // (they're transformed to 1-byte "?" (3f) characters)
    public byte[] replaceSpecialChars(byte[] block, boolean reinsertion){
        byte[] block2 = new byte[2];

        byte[] origI = new byte[]{(byte) 0x87, (byte) 0x54};
        byte[] origII = new byte[]{(byte) 0x87, (byte) 0x55};
        byte[] origIII = new byte[]{(byte) 0x87, (byte) 0x56};
        byte[] origGameo = new byte[]{(byte) 0xeb, (byte) 0x3f};

        byte[] newI = new byte[]{(byte) 0x83, (byte) 0xa7};
        byte[] newII = new byte[]{(byte) 0x84, (byte) 0x50};
        byte[] newIII = new byte[]{(byte) 0x84, (byte) 0x59};
        byte[] newGameo = new byte[]{(byte) 0x84, (byte) 0x5f};

        boolean stop = false;

        for (int i = 0; i < block.length && !stop; i++){
            block2[0] = block[i];
            block2[1] = block[i + 1];

            if (!reinsertion){
                if (block2[0] == origI[0] && block2[1] == origI[1]){
                    block[i] = newI[0];
                    block[i + 1] = newI[1];
                }
                else if (block2[0] == origII[0] && block2[1] == origII[1]){
                    block[i] = newII[0];
                    block[i + 1] = newII[1];
                }
                else if (block2[0] == origIII[0] && block2[1] == origIII[1]){
                    block[i] = newIII[0];
                    block[i + 1] = newIII[1];
                }
                else if (block2[0] == origGameo[0] && block2[1] == origGameo[1]){
                    block[i] = newGameo[0];
                    block[i + 1] = newGameo[1];
                }
            }
            else{
                if (block2[0] == newI[0] && block2[1] == newI[1]){
                    block[i] = origI[0];
                    block[i + 1] = origI[1];
                }
                else if (block2[0] == newII[0] && block2[1] == newII[1]){
                    block[i] = origII[0];
                    block[i + 1] = origII[1];
                }
                else if (block2[0] == newIII[0] && block2[1] == newIII[1]){
                    block[i] = origIII[0];
                    block[i + 1] = origIII[1];
                }
                else if (block2[0] == newGameo[0] && block2[1] == newGameo[1]){
                    block[i] = origGameo[0];
                    block[i + 1] = origGameo[1];
                }
            }

            // There are some titles that end with 3 1-byte characters, so we have to
            // control we're not getting out of the block
            if (i + 2 > block.length - 1)
                stop = true;
        }

        return block;
    }

    
    // Enables / disables certain fields of the UI
    // All fields affected by this method are ones that either have an unknown effect
    // or are determined risky to modify
    // Disable safety only if you know what you're doing!
    public void applySafety(){
        boolean safety = !checkItemSafety.isSelected();
        
        // Set safety in Units tab
	fieldEssential.setEditable(safety);
	fieldLibID.setEditable(safety);
	fieldModelID.setEditable(safety);
	fieldByte37.setEditable(safety);
	fieldByte38.setEditable(safety);
	fieldByte39.setEditable(safety);
	fieldByte56.setEditable(safety);
	fieldByte60.setEditable(safety);
	fieldByte61.setEditable(safety);
	fieldByte62.setEditable(safety);
	fieldByte69.setEditable(safety);
	fieldByte71.setEditable(safety);
	fieldByte72.setEditable(safety);
        
        // Set safety in Characters tab
	fieldLibIDChar.setEditable(safety);
	fieldPortrait.setEditable(safety);
	checkSkill12.setEnabled(safety);
	checkSkill13.setEnabled(safety);
	checkSkill14.setEnabled(safety);
	checkSkill15.setEnabled(safety);
	checkSkill16.setEnabled(safety);
	fieldByteChar70.setEditable(safety);
	fieldByteChar71.setEditable(safety);
	fieldByteChar72.setEditable(safety);
	fieldByteChar79.setEditable(safety);
	fieldByteChar80.setEditable(safety);
	fieldByteChar81.setEditable(safety);
	fieldByteChar82.setEditable(safety);
	fieldByteChar83.setEditable(safety);
	fieldByteChar84.setEditable(safety);
        
        // Set safety in Weapon panels
        Component[] panels = panelWeapList.getComponents();
        
        for (int i = 0; i < panels.length; i++){
            WeaponPanel wp = (WeaponPanel) panels[i];
            wp.setSafety(safety);
        }
        
        //this.revalidate();  // repaint?
    }
    
    
    // Ensure textfield's value doesn't go over a given limit
    private void capField(JTextField t, int limit){
        if (!t.getText().isEmpty()){            
            int value = Integer.valueOf(t.getText());
            
            if (value > limit)
                t.setText("" + limit);
        }
    }
    
    /*********************************************************/
    /******************* I/O and parsing *********************/
    /*********************************************************/
    
    // Opens the selected file (add02dat.bin)
    public void openBinFile(){
        String bin_file = "";
        
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(lastDirectory));
        chooser.setDialogTitle("Load add02dat.bin file");
        chooser.setFileFilter(new FileNameExtensionFilter("BIN file", "bin"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            bin_file = chooser.getSelectedFile().getAbsolutePath();

            lastDirectory = chooser.getSelectedFile().getPath();
            current_file = chooser.getSelectedFile().getName();
            
            parseFile(bin_file);
            
            current_unit = 0;
            current_char = 0;
            loadUnit();
            loadChar();
            
            applySafety();
        }        
    }
    
    // Parse add02dat.bin and put its content into our data structures
    public void parseFile(String path){

        RandomAccessFile f;

        // Try opening the file
        try{
            f = new RandomAccessFile(path, "r");

            // Read the header / index and obtain the offsets
            block_offsets = new int[123];

            byte[] block4 = new byte[4];  // 4-byte block

            for (int i = 0; i < 123; i ++){
                f.read(block4);
                block_offsets[i] = parse4bytes(block4);
            }
            f.seek(512);    // Go to right after the first table, past the padding

            // Parse each block, according to its content
                /*
                 * Worthwhile blocks:
                    0 char full names - 337
                    1 char names - 337
                    2 char data - 337 entries

                    5 unit names - 276
                    6 unit data - 276 entries

                    8 weapon names - 946
                    9 weapon data - 946 entries
                    10 assignment of weapons to units  276 entries
                    11 configuration of combination attacks - 42 entries (last one dummy)

                    13 terrain names - 189

                    21 seishin names - 30
                    22 seishin short names - 30
                    23 seishin descriptions - 30

                    25 parts names - 44
                    26 parts descriptions - 44

                    28 pilot parts - 37
                    29 pilot parts descriptions - 37

                    33 stage names - 100

                    38 frame names (2-byte pointers table, several frames per unit) - 5

                    40 char library (8 ??? bytes, name, actor, description) - 267

                    41 robot library (10 ??? bytes, name?, size, weight, description) - 238
                 */
            byte[] block_read;
            int d_counter = 0;

            d_blocks = new byte[120][];

            for (int i = 0; i < 123; i++){
                if (i == 122){  // Last data block
                    block_read = new byte[32];  // Last block is always 32 bytes long
                    f.read(block_read);

                    d_blocks[d_counter] = block_read;
                    // no need to increase the counter, this is the last one
                }
                else{
                    // Determine the size of the block
                    int block_size = block_offsets[i+1] - block_offsets[i];
                    //System.out.println("Block size: " + block_size);

                    block_read = new byte[block_size];
                    f.read(block_read);
                    //System.out.println("Byte 3: " + (block_read[2] & 0xff) + " - Byte 4: " + (block_read[3] & 0xff));

                    // Classify the block
                    if (i == 0){
                        charNames = parseNamesBlock(block_read);
                        setValuesComboBox(charNames, comboChars);
                        
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                    else if (i == 2){   // Char data
                        parseChars(block_read);
                        
                        // Consistency check
                        //byte[] aux = getCharactersBytes();
                        //boolean blnResult = Arrays.equals(block_read, aux);
                        //System.out.println("Characters: Can we write the same as what we read? : " + blnResult);
                    }
                    else if (i == 5){
                        unitNames = parseNamesBlock(block_read);
                        setValuesComboBox(unitNames, comboUnits);
                        
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                    else if (i == 6){   // Unit data
                        parseUnits(block_read);
                        
                        // Consistency check
                        //byte[] aux = getUnitsBytes();
                        //boolean blnResult = Arrays.equals(block_read, aux);
                        //System.out.println("Units: Can we write the same as what we read? : " + blnResult);
                        
                    }
                    else if (i == 8){   // Weapon names
                        weapNames = parseNamesBlock(block_read);
                        
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                    else if (i == 9){   // Weapon data
                        parseWeapons(block_read);
                        
                        // Consistency check
                        //byte[] aux = getWeaponsBytes();
                        //boolean blnResult = Arrays.equals(block_read, aux);
                        //System.out.println("Weapons: Can we write the same as what we read? : " + blnResult);
                    }
                    else if (i == 10){   // Weapon assignment
                        parseWeaponAssignment(block_read);
                        
                        // We don't modify this, so we save it as a data block too
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                    else if (i == 21){
                        commNames = parseNamesBlock(block_read);
                        setValuesComboBox(commNames, comboCommand1);
                        setValuesComboBox(commNames, comboCommand2);
                        setValuesComboBox(commNames, comboCommand3);
                        setValuesComboBox(commNames, comboCommand4);
                        setValuesComboBox(commNames, comboCommand5);
                        setValuesComboBox(commNames, comboCommand6);
                        
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                    else if (i == 25){
                        enhaNames = parseNamesBlock(block_read);
                        setValuesComboBox(enhaNames, comboItem);
                        
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                    else if (i == 28){  // Skill parts names
                        skilNames = parseNamesBlock(block_read);    // <--- It seems this is not used in the end
                        
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                    else{   // Data blocks
                        d_blocks[d_counter] = block_read;
                        d_counter++;
                    }
                }
            }

            // Finished parsing, close the file
            f.close();

            file_loaded = true; // Not really needed
            
            menuExport.setEnabled(file_loaded);
            itemUnitsExport.setEnabled(file_loaded);
            itemWeaponsExport.setEnabled(file_loaded);
            itemCharactersExport.setEnabled(file_loaded);
            menuImport.setEnabled(file_loaded);
            itemUnitsImport.setEnabled(file_loaded);
            itemWeaponsImport.setEnabled(file_loaded);
            itemCharactersImport.setEnabled(file_loaded);
            itemSaveBin.setEnabled(file_loaded);
            
            JOptionPane.showMessageDialog(null, "File " + current_file + " processed correctly.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException | NegativeArraySizeException | ArrayIndexOutOfBoundsException
                | NumberFormatException | NullPointerException ex) {
            System.err.println("ERROR: Couldn't read file.");   // END
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Something went wrong. Probably tried to load the wrong file.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    public void parseUnits(byte[] block){
        // There are 276 units in this block (or should be at least)
        // Each entry has 72 bytes
        // There could be padding at the end, but we won't get to read that
        int offset = 0;
        
        units = new UnitData[unitNames.length];        
        
        // We read 72 bytes 276 times
        for (int i = 0; i < units.length; i++){
            units[i] = new UnitData();
            
            units[i].abilities = subBlock( block, offset, 4 );
            offset += 4;
            
            units[i].bodyType = block[offset];
            offset++;
            units[i].headType = block[offset];
            offset++;
            units[i].armsType = block[offset];
            offset++;
            units[i].legsType = block[offset];
            offset++;
            
            units[i].bodyHP = parse4bytes( subBlock( block, offset, 4 ) );
            offset += 4;
            units[i].headHP = parse4bytes( subBlock( block, offset, 4 ) );
            offset += 4;
            units[i].armsHP = parse4bytes( subBlock( block, offset, 4 ) );
            offset += 4;
            units[i].legsHP = parse4bytes( subBlock( block, offset, 4 ) );
            offset += 4;
            
            units[i].EN = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            units[i].terrainRatings = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            units[i].mobility = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            units[i].armor = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            units[i].repairCost = (short) ( parse2bytes( subBlock( block, offset, 2 ) ) & 0xffff );
            offset += 2;
            units[i].reward = (short) ( parse2bytes( subBlock( block, offset, 2 ) ) & 0xffff );
            offset += 2;
            
            units[i].byte37 = block[offset];
            offset++;
            units[i].byte38 = block[offset];
            offset++;
            units[i].byte39 = block[offset];
            offset++;
            
            units[i].enhanceParts = block[offset];
            offset++;
            units[i].size = block[offset];
            offset++;
            units[i].movement = block[offset];
            offset++;
            units[i].terrainType = block[offset];
            offset++;
            
            units[i].enemyAI = block[offset];
            offset++;
            
            units[i].pilotFamily = subBlock( block, offset, 2 );
            offset += 2;
            
            units[i].bgm = block[offset];
            offset++;
            units[i].shield = block[offset];
            offset++;
            
            units[i].upgradesHP = block[offset];
            offset++;
            units[i].upgradesEN = block[offset];
            offset++;
            units[i].upgradesArmor = block[offset];
            offset++;
            units[i].upgradesMobility = block[offset];
            offset++;
            units[i].upgradesWeapons = block[offset];
            offset++;
            
            units[i].inflationWeapons = block[offset];
            offset++;
            
            units[i].capture = block[offset];
            offset++;
            
            units[i].byte56 = block[offset];
            offset++;
            
            units[i].item = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            units[i].essential = block[offset];
            offset++;
            
            units[i].byte60 = block[offset];
            offset++;
            units[i].byte61 = block[offset];
            offset++;
            units[i].byte62 = block[offset];
            offset++;
            
            units[i].libraryID = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            units[i].model3D = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            units[i].series = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            units[i].byte69 = block[offset];
            offset++;
            
            units[i].building = block[offset];
            offset++;
            
            units[i].byte71 = block[offset];
            offset++;
            units[i].byte72 = block[offset];
            offset++;
        }        
    }
    
    
    public void parseWeapons(byte[] block){
        // There are 946 weapons in this block (or should be at least)
        // Each entry has 24 bytes
        // There could be padding at the end, but we won't get to read that
        int offset = 0;
        
        weapons = new WeaponData[weapNames.length];        
        
        // We read 24 bytes 946 times
        for (int i = 0; i < weapons.length; i++){
            weapons[i] = new WeaponData();
            
            weapons[i].byte01 = block[offset];
            offset++;
            
            weapons[i].properties = subBlock( block, offset, 2 );
            offset += 2;
            weapons[i].type = block[offset];
            offset++;
            
            weapons[i].power = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            weapons[i].EN = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            weapons[i].part = block[offset];
            offset++;
            
            weapons[i].byte10 = block[offset];
            offset++;
            
            weapons[i].terrainRatings = subBlock( block, offset, 2 );
            offset += 2;
            
            weapons[i].hit = block[offset];
            offset++;
            weapons[i].critical = block[offset];
            offset++;
            weapons[i].ammo = block[offset];
            offset++;
            weapons[i].will = block[offset];
            offset++;
            weapons[i].rangeMin = block[offset];
            offset++;
            weapons[i].rangeMax = block[offset];
            offset++;
            weapons[i].bgm = block[offset];
            offset++;
            weapons[i].combo = block[offset];
            offset++;
            
            weapons[i].byte21 = block[offset];
            offset++;
            
            weapons[i].animation = block[offset];
            offset++;
            
            weapons[i].byte23 = block[offset];
            offset++;
            weapons[i].byte24 = block[offset];
            offset++;
        }
        
    }
    
    
    public void parseWeaponAssignment(byte[] block){
        /*
         * There are 276 entries for Weapon Assignment (table size = 1104), one per unit
         * Each entry has byte at the beginning indicating how many weapons are assigned
         * Each weapon is represented by a 2-byte ID
         */

        // First, get the table
        int[] entries = new int[unitNames.length];

        int offset = 0;
        short weapID = 0;

        for (int i = 0; i < unitNames.length; i++){
            entries[i] = parse4bytes( subBlock( block, offset, 4 ) );
            offset += 4;
        }

        // For each entry, get the weapons inside
        byte numWeapons;

        for (int i = 0; i < entries.length; i++){
            offset = entries[i];
            numWeapons = block[offset];
            offset++;
            
            units[i].weapons = new short[numWeapons];
            
            for (int j = 0; j < numWeapons; j++){
                weapID = (short) parse2bytes( subBlock (block, offset, 2) );
                offset += 2;
                
                // Assign weapon to our unit & viceversa
                units[i].weapons[j] = weapID;
                weapons[weapID].belongToUnit = (short) i;
            }
        }
        
    }
    
    
    public void parseChars(byte[] block){
        // There are 337 units in this block (or should be at least)
        // Each entry has 84 bytes
        // There could be padding at the end, but we won't get to read that
        int offset = 0;
        
        characters = new CharacterData[charNames.length];        
        
        // We read 84 bytes 337 times
        for (int i = 0; i < characters.length; i++){
            characters[i] = new CharacterData();
            
            characters[i].personality = block[offset];
            offset++;
            characters[i].ally = block[offset];
            offset++;
            
            characters[i].melee = block[offset];
            offset++;
            characters[i].ranged = block[offset];
            offset++;
            characters[i].defense = block[offset];
            offset++;
            characters[i].skill = block[offset];
            offset++;
            characters[i].evasion = block[offset];
            offset++;
            characters[i].accuracy = block[offset];
            offset++;
            
            characters[i].skills = subBlock( block, offset, 2 );
            offset += 2;
            
            characters[i].ntLv1 = block[offset];
            offset++;
            characters[i].ntLv2 = block[offset];
            offset++;
            characters[i].ntLv3 = block[offset];
            offset++;
            characters[i].ntLv4 = block[offset];
            offset++;
            characters[i].ntLv5 = block[offset];
            offset++;
            characters[i].ntLv6 = block[offset];
            offset++;
            characters[i].ntLv7 = block[offset];
            offset++;
            characters[i].ntLv8 = block[offset];
            offset++;
            characters[i].ntLv9 = block[offset];
            offset++;
            
            characters[i].potentialLv1 = block[offset];
            offset++;
            characters[i].potentialLv2 = block[offset];
            offset++;
            characters[i].potentialLv3 = block[offset];
            offset++;
            characters[i].potentialLv4 = block[offset];
            offset++;
            characters[i].potentialLv5 = block[offset];
            offset++;
            characters[i].potentialLv6 = block[offset];
            offset++;
            characters[i].potentialLv7 = block[offset];
            offset++;
            characters[i].potentialLv8 = block[offset];
            offset++;
            characters[i].potentialLv9 = block[offset];
            offset++;
            
            characters[i].supportLv1 = block[offset];
            offset++;
            characters[i].supportLv2 = block[offset];
            offset++;
            characters[i].supportLv3 = block[offset];
            offset++;
            characters[i].supportLv4 = block[offset];
            offset++;
            
            characters[i].commandLv1 = block[offset];
            offset++;
            characters[i].commandLv2 = block[offset];
            offset++;
            characters[i].commandLv3 = block[offset];
            offset++;
            characters[i].commandLv4 = block[offset];
            offset++;
			
            characters[i].pilotFamily = subBlock( block, offset, 2 );
            offset += 2;
            
            characters[i].skillAces = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            characters[i].enemyAI = block[offset];
            offset++;
            
            characters[i].parts = block[offset];
            offset++;
			
            characters[i].SP = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            characters[i].seishin1_lv = block[offset];
            offset++;
            characters[i].seishin1_id = block[offset];
            offset++;			
            characters[i].seishin1_cost = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            characters[i].seishin2_lv = block[offset];
            offset++;
            characters[i].seishin2_id = block[offset];
            offset++;			
            characters[i].seishin2_cost = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            characters[i].seishin3_lv = block[offset];
            offset++;
            characters[i].seishin3_id = block[offset];
            offset++;			
            characters[i].seishin3_cost = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            characters[i].seishin4_lv = block[offset];
            offset++;
            characters[i].seishin4_id = block[offset];
            offset++;			
            characters[i].seishin4_cost = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            characters[i].seishin5_lv = block[offset];
            offset++;
            characters[i].seishin5_id = block[offset];
            offset++;			
            characters[i].seishin5_cost = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            characters[i].seishin6_lv = block[offset];
            offset++;
            characters[i].seishin6_id = block[offset];
            offset++;			
            characters[i].seishin6_cost = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            characters[i].growthSchema = block[offset];
            offset++;
            
            characters[i].byte70 = block[offset];
            offset++;
            characters[i].byte71 = block[offset];
            offset++;
            characters[i].byte72 = block[offset];
            offset++;
			
            characters[i].libraryID = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            characters[i].portrait = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            characters[i].series = (short) parse2bytes( subBlock( block, offset, 2 ) );
            offset += 2;
            
            characters[i].byte79 = block[offset];
            offset++;
            characters[i].byte80 = block[offset];
            offset++;
            characters[i].byte81 = block[offset];
            offset++;
            characters[i].byte82 = block[offset];
            offset++;
            characters[i].byte83 = block[offset];
            offset++;
            characters[i].byte84 = block[offset];
            offset++;
        }
        
    }

    
    // Formats a block of bytes (table + names) into an array of String
    public String[] parseNamesBlock(byte[] block){
        //System.out.println("Parsing block: " + position);
        String[] names = new String[1];
        
        // Get the size of the table
        byte[] parse_block = subBlock(block, 0, 4);

        if (parse_block == null)
            return names;

        int offset = parse4bytes(parse_block);

        //tb.setTableSize(offset);

        int num_entries = offset / 4;
        
        names = new String[num_entries];

        //System.out.println("Num. entries: " + num_entries);

        int next_offset = parse4bytes( subBlock( block, 4, 4 ) );

        int size = 0;   // Size of an entry = next_offset - offset

        for (int i = 0; i < num_entries - 1; i++){
            //System.out.println("Entry: " + i + " Offset: " + offset + " - Next: " + next_offset);
            size = (next_offset - offset) - 2;  // We don't take the separator
            parse_block = subBlock(block, offset, size);

            parse_block = replaceSpecialChars(parse_block, false);  // Replace special characters!

            names[i] = hex2string(parse_block);  // Save the SJIS text

            offset = next_offset;
            if (i != num_entries - 2){   // We leave the last one out because there's no next offset there
                next_offset = parse4bytes( subBlock( block, (i + 2)*4, 4 ) );
            }
        }
        // Read the last entry
        names[num_entries - 1] = hex2string(replaceSpecialChars( findLastString(block, offset, 0, 1), false ) );

        return names;
    }
    
    
    // Load info of selected unit into the GUI
    public void loadUnit(){
        setAbil( units[current_unit].abilities );
        
        setComboBody( units[current_unit].bodyType );
        setComboHead( units[current_unit].headType );
        setComboArms( units[current_unit].armsType );
        setComboLegs( units[current_unit].legsType );
        
        setBodyBase( units[current_unit].bodyHP );
        setHeadBase( units[current_unit].headHP );
        setArmsBase( units[current_unit].armsHP );
        setLegsBase( units[current_unit].legsHP );
        
        setEnergyBase( units[current_unit].EN );
        
        setTerrain( short2bytes( units[current_unit].terrainRatings ) );
        
        setMobilityBase( units[current_unit].mobility );
        setArmorBase( units[current_unit].armor );
        
        setRepair( (int) ( units[current_unit].repairCost & 0xffff ) );     // Cast to an int to avoid negative values
        setReward( (int) ( units[current_unit].reward & 0xffff ) );     // Cast to an int to avoid negative values
        
        setByte37( units[current_unit].byte37 );
        setByte38( units[current_unit].byte38 );
        setByte39( units[current_unit].byte39 );
        
        setComboParts( units[current_unit].enhanceParts );
        setComboSize( units[current_unit].size );
        setComboMove( units[current_unit].movement );
        setTerrainType( units[current_unit].terrainType );
        
        setAI( units[current_unit].enemyAI );
        
        setFamUnit( units[current_unit].pilotFamily );
        
        setComboBGM( units[current_unit].bgm );
        setShield( units[current_unit].shield );
        
        setComboUpgradesHP( units[current_unit].upgradesHP );
        setComboUpgradesEN( units[current_unit].upgradesEN );
        setComboUpgradesMob( units[current_unit].upgradesMobility );
        setComboUpgradesArmor( units[current_unit].upgradesArmor );
        
        loadWeapons();
        setComboUpgradesWeapons( units[current_unit].upgradesWeapons );
        
        setInflation( units[current_unit].inflationWeapons );
        
        setComboCapture( units[current_unit].capture );
        
        setByte56( units[current_unit].byte56 );
        
        setComboItem( units[current_unit].item );
        setEssential( units[current_unit].essential );
        
        setByte60( units[current_unit].byte60 );
        setByte61( units[current_unit].byte61 );
        setByte62( units[current_unit].byte62 );
        
        setLibID( units[current_unit].libraryID );
        setModelID( units[current_unit].model3D );
        setComboSeries( units[current_unit].series );
        
        setByte69( units[current_unit].byte69 );
        
        setBuilding( units[current_unit].building );
        
        setByte71( units[current_unit].byte71 );
        setByte72( units[current_unit].byte72 );
    }
    
    // Load info of weapons for selected unit into the GUI
    public void loadWeapons(){
        panelWeapList.removeAll();  // Clean panel
        
        int numWeapons = units[current_unit].weapons.length;
        int weapID = 0;
        
        panelWeapList.setPreferredSize( new Dimension( 485, 389 * numWeapons ) );   // Make room
        
        for ( int i = 0; i < numWeapons ; i++ ){
            WeaponPanel wp = new WeaponPanel();
            
            wp.setBounds(1, 1 + (387 * i), 500, 386);
            
            // Set values
            weapID = units[current_unit].weapons[i];
            
            wp.setID(weapID);
            wp.setWeapName(weapNames[weapID]);
            
            wp.setByte01(weapons[weapID].byte01);
            
            wp.setProp(weapons[weapID].properties);
            wp.setType(weapons[weapID].type);
            
            wp.setPower(weapons[weapID].power);
            wp.setEN(weapons[weapID].EN);
            
            wp.setMount(weapons[weapID].part);
            
            wp.setByte10(weapons[weapID].byte10);
            
            wp.setTerrain(weapons[weapID].terrainRatings);
            
            wp.setHit(weapons[weapID].hit);
            wp.setCrit(weapons[weapID].critical);
            wp.setAmmo(weapons[weapID].ammo);
            wp.setWill(weapons[weapID].will);
            wp.setMinRange(weapons[weapID].rangeMin);
            wp.setMaxRange(weapons[weapID].rangeMax);
            wp.setBGM(weapons[weapID].bgm); 
            //System.out.println("Weapon " + weapID + " - BGM: " + weapons[weapID].bgm);
            wp.setComboID(weapons[weapID].combo);
            
            wp.setByte21(weapons[weapID].byte21);
            
            wp.setAnim(weapons[weapID].animation);
            
            wp.setByte23(weapons[weapID].byte23);
            wp.setByte24(weapons[weapID].byte24);
            
            // Apply safety
            wp.setSafety(checkItemSafety.isSelected());
            
            // Add to panel
            panelWeapList.add(wp);
        }
        
        // Repaint scroll panel and all its components
        scrollWeapons.revalidate();
    }
    
    // Load info of selected character into the GUI
    public void loadChar(){
        setComboPersonality(characters[current_char].personality);
        setComboAlly(characters[current_char].ally);
        
        setMeleeBase(characters[current_char].melee);
        setRangedBase(characters[current_char].ranged);
        setDefenseBase(characters[current_char].defense);
        setSkillBase(characters[current_char].skill);
        setEvasionBase(characters[current_char].evasion);
        setAccuracyBase(characters[current_char].accuracy);
        
        setSkill(characters[current_char].skills);
        
        setNTlv1(characters[current_char].ntLv1);
        setNTlv2(characters[current_char].ntLv2);
        setNTlv3(characters[current_char].ntLv3);
        setNTlv4(characters[current_char].ntLv4);
        setNTlv5(characters[current_char].ntLv5);
        setNTlv6(characters[current_char].ntLv6);
        setNTlv7(characters[current_char].ntLv7);
        setNTlv8(characters[current_char].ntLv8);
        setNTlv9(characters[current_char].ntLv9);
        
        setPotentialLv1(characters[current_char].potentialLv1);
        setPotentialLv2(characters[current_char].potentialLv2);
        setPotentialLv3(characters[current_char].potentialLv3);
        setPotentialLv4(characters[current_char].potentialLv4);
        setPotentialLv5(characters[current_char].potentialLv5);
        setPotentialLv6(characters[current_char].potentialLv6);
        setPotentialLv7(characters[current_char].potentialLv7);
        setPotentialLv8(characters[current_char].potentialLv8);
        setPotentialLv9(characters[current_char].potentialLv9);
        
        setSupportLv1(characters[current_char].supportLv1);
        setSupportLv2(characters[current_char].supportLv2);
        setSupportLv3(characters[current_char].supportLv3);
        setSupportLv4(characters[current_char].supportLv4);
        
        setCommandLv1(characters[current_char].commandLv1);
        setCommandLv2(characters[current_char].commandLv2);
        setCommandLv3(characters[current_char].commandLv3);
        setCommandLv4(characters[current_char].commandLv4);
        
        setFamChar(characters[current_char].pilotFamily);
        
        setComboSkillAces(characters[current_char].skillAces);
        
        setEnemyAI(characters[current_char].enemyAI);
        
        setComboSkillParts(characters[current_char].parts);
        
        setSPBase(characters[current_char].SP);
        
        setComboCommand1(characters[current_char].seishin1_id);
        setCostCommand1(characters[current_char].seishin1_cost);
        setLearnCommand1(characters[current_char].seishin1_lv);
        setComboCommand2(characters[current_char].seishin2_id);
        setCostCommand2(characters[current_char].seishin2_cost);
        setLearnCommand2(characters[current_char].seishin2_lv);
        setComboCommand3(characters[current_char].seishin3_id);
        setCostCommand3(characters[current_char].seishin3_cost);
        setLearnCommand3(characters[current_char].seishin3_lv);
        setComboCommand4(characters[current_char].seishin4_id);
        setCostCommand4(characters[current_char].seishin4_cost);
        setLearnCommand4(characters[current_char].seishin4_lv);
        setComboCommand5(characters[current_char].seishin5_id);
        setCostCommand5(characters[current_char].seishin5_cost);
        setLearnCommand5(characters[current_char].seishin5_lv);
        setComboCommand6(characters[current_char].seishin6_id);
        setCostCommand6(characters[current_char].seishin6_cost);
        setLearnCommand6(characters[current_char].seishin6_lv);
        
        setComboGrowthSchema(characters[current_char].growthSchema);
        
        setByteChar70(characters[current_char].byte70);
        setByteChar71(characters[current_char].byte71);
        setByteChar72(characters[current_char].byte72);
        
        setLibIDChar(characters[current_char].libraryID);
        setPortrait(characters[current_char].portrait);
        setComboSeriesChar(characters[current_char].series);
        
        setByteChar79(characters[current_char].byte79);
        setByteChar80(characters[current_char].byte80);
        setByteChar81(characters[current_char].byte81);
        setByteChar82(characters[current_char].byte82);
        setByteChar83(characters[current_char].byte83);
        setByteChar84(characters[current_char].byte84);
    }
    
    // Saves currently displayed unit
    public void saveUnit(){
        saveWeapons();
        
        units[current_unit].abilities = getAbil();
        
        units[current_unit].bodyType = (byte) getComboBody();
        units[current_unit].headType = (byte) getComboHead();
        units[current_unit].armsType = (byte) getComboArms();
        units[current_unit].legsType = (byte) getComboLegs();
        
        units[current_unit].bodyHP = getBodyBase();
        units[current_unit].headHP = getHeadBase();
        units[current_unit].armsHP = getArmsBase();
        units[current_unit].legsHP = getLegsBase();
        
        units[current_unit].EN = (short) getEnergyBase();
        
        units[current_unit].terrainRatings = (short) parse2bytes( getTerrain() );
        
        units[current_unit].mobility = (short) getMobilityBase();
        units[current_unit].armor = (short) getArmorBase();
        
        units[current_unit].repairCost = (short) getRepair();
        units[current_unit].reward = (short) getReward();
        
        units[current_unit].byte37 = (byte) getByte37();
        units[current_unit].byte38 = (byte) getByte38();
        units[current_unit].byte39 = (byte) getByte39();
        
        units[current_unit].enhanceParts = (byte) getComboParts();
        units[current_unit].size = (byte) getComboSize();
        units[current_unit].movement = (byte) getComboMove();
        units[current_unit].terrainType = getTerrainType();
        
        units[current_unit].enemyAI = (byte) getAI();
        
        units[current_unit].pilotFamily = getFamUnit();
        
        units[current_unit].bgm = (byte) getComboBGM();
        units[current_unit].shield = getShield();
        
        units[current_unit].upgradesHP = (byte) getComboUpgradesHP();
        units[current_unit].upgradesEN = (byte) getComboUpgradesEN();
        units[current_unit].upgradesMobility = (byte) getComboUpgradesMob();
        units[current_unit].upgradesArmor = (byte) getComboUpgradesArmor();
        
        units[current_unit].upgradesWeapons = (byte) getComboUpgradesWeapons();
        
        units[current_unit].inflationWeapons = (byte) getInflation();
        
        units[current_unit].capture = (byte) getComboCapture();
        
        units[current_unit].byte56 = (byte) getByte56();
        
        units[current_unit].item = (short) getComboItem();
        units[current_unit].essential = (byte) getEssential();
        
        units[current_unit].byte60 = (byte) getByte60();
        units[current_unit].byte61 = (byte) getByte61();
        units[current_unit].byte62 = (byte) getByte62();
        
        units[current_unit].libraryID = (short) getLibID();
        units[current_unit].model3D = (short) getModelID();
        units[current_unit].series = (byte) getComboSeries();
        
        units[current_unit].byte69 = (byte) getByte69();
        
        units[current_unit].building = (byte) getBuilding();
        
        units[current_unit].byte71 = (byte) getByte71();
        units[current_unit].byte72 = (byte) getByte72();
    }
    
    // Saves weapons of currently displayed unit
    public void saveWeapons(){
        Component[] panels = panelWeapList.getComponents();
        
        int weapID = 0;
        
        for (int i = 0; i < panels.length; i++){
            WeaponPanel wp = (WeaponPanel) panels[i];
            
            // Get weapon ID first
            weapID = units[current_unit].weapons[i];
            
            // Save values
            weapons[weapID].byte01 = (byte) wp.getByte01();
            
            weapons[weapID].properties = wp.getProp();
            weapons[weapID].type = wp.getType();
            
            weapons[weapID].power = (short) wp.getPower();
            weapons[weapID].EN = (short) wp.getEN();
            
            weapons[weapID].part = (byte) wp.getMount();
            
            weapons[weapID].byte10 = (byte) wp.getByte10();
            
            weapons[weapID].terrainRatings = wp.getTerrain();
            
            weapons[weapID].hit = (byte) wp.getHit();
            weapons[weapID].critical = (byte) wp.getCrit();
            weapons[weapID].ammo = (byte) wp.getAmmo();
            weapons[weapID].will = (byte) wp.getWill();
            weapons[weapID].rangeMin = (byte) wp.getMinRange();
            weapons[weapID].rangeMax = (byte) wp.getMaxRange();
            weapons[weapID].bgm = (byte) wp.getBGM();
            weapons[weapID].combo = (byte) wp.getComboID();
            
            weapons[weapID].byte21 = (byte) wp.getByte21();
            
            weapons[weapID].animation = (byte) wp.getAnim();
            
            weapons[weapID].byte23 = (byte) wp.getByte23();
            weapons[weapID].byte24 = (byte) wp.getByte24();
        }
    }
    
    // Saves currently displayed character
    public void saveChar(){
        characters[current_char].personality = (byte) getComboPersonality();
        characters[current_char].ally = (byte) getComboAlly();
        
        characters[current_char].melee = (byte) getMeleeBase();
        characters[current_char].ranged = (byte) getRangedBase();
        characters[current_char].defense = (byte) getDefenseBase();
        characters[current_char].skill = (byte) getSkillBase();
        characters[current_char].evasion = (byte) getEvasionBase();
        characters[current_char].accuracy = (byte) getAccuracyBase();
        
        characters[current_char].skills = getSkill();
        
        characters[current_char].ntLv1 = (byte) getNTlv1();
        characters[current_char].ntLv2 = (byte) getNTlv2();
        characters[current_char].ntLv3 = (byte) getNTlv3();
        characters[current_char].ntLv4 = (byte) getNTlv4();
        characters[current_char].ntLv5 = (byte) getNTlv5();
        characters[current_char].ntLv6 = (byte) getNTlv6();
        characters[current_char].ntLv7 = (byte) getNTlv7();
        characters[current_char].ntLv8 = (byte) getNTlv8();
        characters[current_char].ntLv9 = (byte) getNTlv9();
        
        characters[current_char].potentialLv1 = (byte) getPotentialLv1();
        characters[current_char].potentialLv2 = (byte) getPotentialLv2();
        characters[current_char].potentialLv3 = (byte) getPotentialLv3();
        characters[current_char].potentialLv4 = (byte) getPotentialLv4();
        characters[current_char].potentialLv5 = (byte) getPotentialLv5();
        characters[current_char].potentialLv6 = (byte) getPotentialLv6();
        characters[current_char].potentialLv7 = (byte) getPotentialLv7();
        characters[current_char].potentialLv8 = (byte) getPotentialLv8();
        characters[current_char].potentialLv9 = (byte) getPotentialLv9();
        
        characters[current_char].supportLv1 = (byte) getSupportLv1();
        characters[current_char].supportLv2 = (byte) getSupportLv2();
        characters[current_char].supportLv3 = (byte) getSupportLv3();
        characters[current_char].supportLv4 = (byte) getSupportLv4();
        
        characters[current_char].commandLv1 = (byte) getCommandLv1();
        characters[current_char].commandLv2 = (byte) getCommandLv2();
        characters[current_char].commandLv3 = (byte) getCommandLv3();
        characters[current_char].commandLv4 = (byte) getCommandLv4();
        
        characters[current_char].pilotFamily = getFamChar();
        
        characters[current_char].skillAces = (short) getComboSkillAces();
        
        characters[current_char].enemyAI = (byte) getEnemyAI();
        
        characters[current_char].parts = (byte) getComboSkillParts();
        
        characters[current_char].SP = (short) getSPBase();
        
        characters[current_char].seishin1_id = (byte) getComboCommand1();
        characters[current_char].seishin1_cost = (short) getCostCommand1();
        characters[current_char].seishin1_lv = (byte) getLearnCommand1();
        characters[current_char].seishin2_id = (byte) getComboCommand2();
        characters[current_char].seishin2_cost = (short) getCostCommand2();
        characters[current_char].seishin2_lv = (byte) getLearnCommand2();
        characters[current_char].seishin3_id = (byte) getComboCommand3();
        characters[current_char].seishin3_cost = (short) getCostCommand3();
        characters[current_char].seishin3_lv = (byte) getLearnCommand3();
        characters[current_char].seishin4_id = (byte) getComboCommand4();
        characters[current_char].seishin4_cost = (short) getCostCommand4();
        characters[current_char].seishin4_lv = (byte) getLearnCommand4();
        characters[current_char].seishin5_id = (byte) getComboCommand5();
        characters[current_char].seishin5_cost = (short) getCostCommand5();
        characters[current_char].seishin5_lv = (byte) getLearnCommand5();
        characters[current_char].seishin6_id = (byte) getComboCommand6();
        characters[current_char].seishin6_cost = (short) getCostCommand6();
        characters[current_char].seishin6_lv = (byte) getLearnCommand6();
        
        characters[current_char].growthSchema = (byte) getComboGrowthSchema();
        
        characters[current_char].byte70 = (byte) getByteChar70();
        characters[current_char].byte71 = (byte) getByteChar71();
        characters[current_char].byte72 = (byte) getByteChar72();
        
        characters[current_char].libraryID = (short) getLibIDChar();
        characters[current_char].portrait = (short) getPortrait();
        characters[current_char].series = (short) getComboSeriesChar();
        
        characters[current_char].byte79 = (byte) getByteChar79();
        characters[current_char].byte80 = (byte) getByteChar80();
        characters[current_char].byte81 = (byte) getByteChar81();
        characters[current_char].byte82 = (byte) getByteChar82();
        characters[current_char].byte83 = (byte) getByteChar83();
        characters[current_char].byte84 = (byte) getByteChar84();
    }
        
    
    // Save the contents of the (modified) file in path (new add02dat.bin)
    public void saveBinFile(){
        
        if (file_loaded){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastDirectory));
            chooser.setDialogTitle("Save a modified add02dat.bin file");
            chooser.setFileFilter(new FileNameExtensionFilter("BIN file", "BIN"));
            if (!current_file.isEmpty())
                chooser.setSelectedFile(new File(current_file));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                current_file = chooser.getSelectedFile().getName();
                lastDirectory = chooser.getSelectedFile().getPath();
                
                try {
                    // 1) Save all currently displayed info
                    saveUnit();
                    saveChar();

                    // 2) Prepare the new master table
                    byte[] table_block = prepareMasterTable();

                    int d_counter = 0;

                    // 3) Write the blocks to the new file
                    RandomAccessFile modfile = new RandomAccessFile(chooser.getSelectedFile().getAbsolutePath(), "rw");

                    modfile.write(table_block); // First the table

                    for (int i = 0; i < 123; i++){  // Then the blocks
                        if (i == 2){  // Character data
                            modfile.write(getCharactersBytes());
                        }
                        else if (i == 6){  // Unit data
                            modfile.write(getUnitsBytes());
                        }
                        else if (i == 9){  // Weapon data
                            modfile.write(getWeaponsBytes());
                        }
                        else{   // Data blocks
                            modfile.write(d_blocks[d_counter]);
                            d_counter++;
                        }
                    }

                    modfile.close();
                    
                    JOptionPane.showMessageDialog(null, "File " + current_file + " saved succesfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException ex) {
                    Logger.getLogger(UserInterfaceUE.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "No file loaded!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    // Returns a 512-byte block with 123 pointers to the blocks in the file
    public byte[] prepareMasterTable(){
        // * All bytes are initialized to zero by default, so there's no need to add padding later
        byte[] block = new byte[512];

        int d_counter = 0;

        byte[] offset;
        int size = 0;
        int accumulated = 512;  // The first pointer aims for the end of the table
        block[2] = 2;   // First pointer is 00 00 02 00

        // Calculate the pointer values
        // Pointer = Accumulated size of data
        // * We don't read the last block - this is NOT a mistake
        // * This is because we read the size of the current block to determine the start of the next one
        // (and obviously, there's no block past the last one)
        for (int i = 0; i < 122; i++){
            if (i == 2){  // Character data
                size = characters.length * 84;
                size = getPaddedSize(size);
            }
            else if (i == 6){  // Unit data
                size = units.length * 72;
                size = getPaddedSize(size);
            }
            else if (i == 9){  // Weapon data
                size = weapons.length * 24;
                size = getPaddedSize(size);
            }
            else{   // Data blocks
                size = d_blocks[d_counter].length;
                d_counter++;
            }

            accumulated += size;
            offset = int2bytes(accumulated);

            block[ (i+1)*4 ] = offset[0];
            block[ (i+1)*4 + 1 ] = offset[1];
            block[ (i+1)*4 + 2 ] = offset[2];
            block[ (i+1)*4 + 3 ] = offset[3];
        }

        return block;
    }
    
    // Returns size ensuring that it's a multiple of 32
    private int getPaddedSize(int size){
        int result = size;
        
        int module = size % 32;
        
        if ( module != 0 ){ // Needs padding
            result += 32 - module;
        }
        
        return result;
    }
    
    // Returns block of unit data for writing
    private byte[] getUnitsBytes(){
        byte[] block;
        int info_size = 72;
        
        int size = units.length * info_size;
        size = getPaddedSize(size);
        
        block = new byte[size];
        
        // Fill the block with our info
        int offset = 0;
        byte[] aux_bytes = new byte[info_size];
        
        for (int i = 0; i< units.length; i++){
            aux_bytes = units[i].getBytes();
            
            for (int j = 0; j < info_size; j++)
                block[offset + j] = aux_bytes[j];
            
            offset += info_size;
        }        
        
        return block;
    }
    
    // Returns block of weapon data for writing
    private byte[] getWeaponsBytes(){
        byte[] block;
        int info_size = 24;
        
        int size = weapons.length * info_size;
        size = getPaddedSize(size);
        
        block = new byte[size];
        
        // Fill the block with our info
        int offset = 0;
        byte[] aux_bytes = new byte[info_size];
        
        for (int i = 0; i< weapons.length; i++){
            aux_bytes = weapons[i].getBytes();
            
            for (int j = 0; j < info_size; j++)
                block[offset + j] = aux_bytes[j];
            
            offset += info_size;
        }
        
        
        return block;
    }
    
    // Returns block of character data for writing
    private byte[] getCharactersBytes(){
        byte[] block;
        int info_size = 84;
        
        int size = characters.length * info_size;
        size = getPaddedSize(size);
        
        block = new byte[size];
        
        // Fill the block with our info
        int offset = 0;
        byte[] aux_bytes = new byte[info_size];
        
        for (int i = 0; i< characters.length; i++){
            aux_bytes = characters[i].getBytes();
            
            for (int j = 0; j < info_size; j++)
                block[offset + j] = aux_bytes[j];
            
            offset += info_size;
        }
        
        
        return block;
    }

    // Creates a TXT file with unit data (internally it's a CSV file)
    private void exportUnitsCSV(){
        String line = "";
        
        if (file_loaded){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastDirectory));
            chooser.setDialogTitle("Export Unit data to TXT file");
            chooser.setFileFilter(new FileNameExtensionFilter("TXT file", "TXT"));
            if (!current_csv.isEmpty())
                chooser.setSelectedFile(new File(current_csv));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                current_csv = chooser.getSelectedFile().getName();
                lastDirectory = chooser.getSelectedFile().getPath();
                
                try {
                    // 1) Save all currently displayed info
                    saveUnit();
                                        
                    // 2) Prepare our writer                    
                    FileWriter writer = new FileWriter( chooser.getSelectedFile().getAbsolutePath() );

                    // 3) Write header
                    writer.append(csv_header_units);
                    
                    // 4) Write each unit's data as a line
                    for (int i = 0; i < units.length; i++){
                        line = String.valueOf(i) + "\t";
                        line += unitNames[i] + "\t";
                        line += units[i].getString() + "\n";
                        writer.append(line);
                    }
                    
                    // 5) Close file
                    writer.close();                
                
                    JOptionPane.showMessageDialog(null, "File " + current_csv + " saved succesfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException ex) {
                    Logger.getLogger(UserInterfaceUE.class.getName()).log(Level.SEVERE, null, ex);
                }          
            }
        }
        else{   // Should neveer reach this message
            JOptionPane.showMessageDialog(null, "No file loaded!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void importUnitsCSV(){
        String bin_file = "";
        
        if (file_loaded){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastDirectory));
            chooser.setDialogTitle("Import Unit data from TXT file");
            chooser.setFileFilter(new FileNameExtensionFilter("TXT file", "txt"));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                bin_file = chooser.getSelectedFile().getAbsolutePath();

                lastDirectory = chooser.getSelectedFile().getPath();
                current_csv = chooser.getSelectedFile().getName();

                try {
                    // 1) Prepare our writer 
                    BufferedReader reader = new BufferedReader ( new FileReader( chooser.getSelectedFile().getAbsolutePath() ) );

                    // 2) Discard first line (header)
                    String line = reader.readLine();                

                    // 3) Process each line in the document
                    for (int i = 0; i < units.length; i++){
                        line = reader.readLine(); 
                        units[i].parseString(line);
                    }

                    // 4) Close the file
                    reader.close();
                    // Any line after the ones expected for units are ignored
                    // If there were less lines than expected, an exception should occur

                    //current_unit = 0;
                    //setComboUnits(current_unit);
                    loadUnit();
                    applySafety();
                    
                    JOptionPane.showMessageDialog(null, "Units imported succesfully from " + current_csv,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException | NegativeArraySizeException | ArrayIndexOutOfBoundsException
                        | NumberFormatException | NullPointerException ex) {
                    Logger.getLogger(UserInterfaceUE.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Something went wrong. Check the file you loaded for invalid data.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }                
        }
        else{   // Should neveer reach this message
            JOptionPane.showMessageDialog(null, "No file loaded!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // Creates a TXT file with weapon data (internally it's a CSV file)
    private void exportWeaponsCSV(){
        String line = "";
        
        if (file_loaded){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastDirectory));
            chooser.setDialogTitle("Export Weapon data to TXT file");
            chooser.setFileFilter(new FileNameExtensionFilter("TXT file", "TXT"));
            if (!current_csv.isEmpty())
                chooser.setSelectedFile(new File(current_csv));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                current_csv = chooser.getSelectedFile().getName();
                lastDirectory = chooser.getSelectedFile().getPath();
                
                try {
                    // 1) Save all currently displayed info
                    saveWeapons();
                                        
                    // 2) Prepare our writer                    
                    FileWriter writer = new FileWriter( chooser.getSelectedFile().getAbsolutePath() );

                    // 3) Write header
                    writer.append(csv_header_weapons);
                    
                    // 4) Write each Weapon's data as a line
                    for (int i = 0; i < weapons.length; i++){
                        line = String.valueOf(weapons[i].belongToUnit) + "\t";
                        line += unitNames[weapons[i].belongToUnit] + "\t";
                        line += String.valueOf(i) + "\t";
                        line += weapNames[i] + "\t";
                        line += weapons[i].getString() + "\n";
                        writer.append(line);
                    }
                    
                    // 5) Close file
                    writer.close();                
                
                    JOptionPane.showMessageDialog(null, "File " + current_csv + " saved succesfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException | NegativeArraySizeException | ArrayIndexOutOfBoundsException
                        | NumberFormatException | NullPointerException ex) {
                    Logger.getLogger(UserInterfaceUE.class.getName()).log(Level.SEVERE, null, ex);
                }          
            }
        }
        else{   // Should neveer reach this message
            JOptionPane.showMessageDialog(null, "No file loaded!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void importWeaponsCSV(){
        String bin_file = "";
        
        if (file_loaded){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastDirectory));
            chooser.setDialogTitle("Import Weapon data from TXT file");
            chooser.setFileFilter(new FileNameExtensionFilter("TXT file", "txt"));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                bin_file = chooser.getSelectedFile().getAbsolutePath();

                lastDirectory = chooser.getSelectedFile().getPath();
                current_csv = chooser.getSelectedFile().getName();

                try {
                    // 1) Prepare our writer 
                    BufferedReader reader = new BufferedReader ( new FileReader( chooser.getSelectedFile().getAbsolutePath() ) );

                    // 2) Discard first line (header)
                    String line = reader.readLine();                

                    // 3) Process each line in the document
                    for (int i = 0; i < weapons.length; i++){
                        line = reader.readLine(); 
                        weapons[i].parseString(line);
                    }

                    // 4) Close the file
                    reader.close();
                    // Any line after the ones expected for Weapons are ignored
                    // If there were less lines than expected, an exception should occur

                    loadWeapons();
                    applySafety();
                    
                    JOptionPane.showMessageDialog(null, "Weapons imported succesfully from " + current_csv,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException | NegativeArraySizeException | ArrayIndexOutOfBoundsException
                        | NumberFormatException | NullPointerException ex) {
                    Logger.getLogger(UserInterfaceUE.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Something went wrong. Check the file you loaded for invalid data.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }          
            }                
        }
        else{   // Should neveer reach this message
            JOptionPane.showMessageDialog(null, "No file loaded!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // Creates a TXT file with character data (internally it's a CSV file)
    private void exportCharactersCSV(){
        String line = "";
        
        if (file_loaded){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastDirectory));
            chooser.setDialogTitle("Export Character data to TXT file");
            chooser.setFileFilter(new FileNameExtensionFilter("TXT file", "TXT"));
            if (!current_csv.isEmpty())
                chooser.setSelectedFile(new File(current_csv));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                current_csv = chooser.getSelectedFile().getName();
                lastDirectory = chooser.getSelectedFile().getPath();
                
                try {
                    // 1) Save all currently displayed info
                    saveUnit();
                                        
                    // 2) Prepare our writer                    
                    FileWriter writer = new FileWriter( chooser.getSelectedFile().getAbsolutePath() );

                    // 3) Write header
                    writer.append(csv_header_characters);
                    
                    // 4) Write each unit's data as a line
                    for (int i = 0; i < characters.length; i++){
                        line = String.valueOf(i) + "\t";
                        line += charNames[i] + "\t";
                        line += characters[i].getString() + "\n";
                        writer.append(line);
                    }
                    
                    // 5) Close file
                    writer.close();                
                
                    JOptionPane.showMessageDialog(null, "File " + current_csv + " saved succesfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException ex) {
                    Logger.getLogger(UserInterfaceUE.class.getName()).log(Level.SEVERE, null, ex);
                }          
            }
        }
        else{   // Should neveer reach this message
            JOptionPane.showMessageDialog(null, "No file loaded!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void importCharactersCSV(){
        String bin_file = "";
        
        if (file_loaded){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(lastDirectory));
            chooser.setDialogTitle("Import Character data from TXT file");
            chooser.setFileFilter(new FileNameExtensionFilter("TXT file", "txt"));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                bin_file = chooser.getSelectedFile().getAbsolutePath();

                lastDirectory = chooser.getSelectedFile().getPath();
                current_csv = chooser.getSelectedFile().getName();

                try {
                    // 1) Prepare our writer 
                    BufferedReader reader = new BufferedReader ( new FileReader( chooser.getSelectedFile().getAbsolutePath() ) );

                    // 2) Discard first line (header)
                    String line = reader.readLine();                

                    // 3) Process each line in the document
                    for (int i = 0; i < characters.length; i++){
                        line = reader.readLine(); 
                        characters[i].parseString(line);
                    }

                    // 4) Close the file
                    reader.close();
                    // Any line after the ones expected for units are ignored
                    // If there were less lines than expected, an exception should occur

                    //current_char = 0;
                    //setComboChars(current_char);
                    loadChar();
                    
                    JOptionPane.showMessageDialog(null, "Characters imported succesfully from " + current_csv,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException | NegativeArraySizeException | ArrayIndexOutOfBoundsException
                        | NumberFormatException | NullPointerException ex) {
                    Logger.getLogger(UserInterfaceUE.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Something went wrong. Check the file you loaded for invalid data.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }          
            }                
        }
        else{   // Should neveer reach this message
            JOptionPane.showMessageDialog(null, "No file loaded!",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    /*********************************************************/
    /*********** GET & SET methods for all fields ************/
    /*********************************************************/
    
    
    // ----------------------------------------------------------------- 
    // ------------------------- GET & SET for ComboBoxes    
    
    
    // --------------------------- Units Tab ---------------------------
    
    
    // Generic set of options for any combobox given an array of string
    private void setValuesComboBox(String[] values, JComboBox cb){
        // Add numeric IDs at the beginning of each string
        // This avoids issues when several entries have the same name
        // (java can get confused if you just move through the values with the arrow keys)
        
        // Create a copy of the list we've been given (don't overwrite the original!)
        String[] copy = new String[values.length];
        
        for (int i = 0; i < values.length; i++){
            if (values.length < 10){
                copy[i] = (i + " " + values[i]);
            }
            else if (values.length < 100){
                if (i < 10)
                    copy[i] = ("0" + i + " " + values[i]);
                else
                    copy[i] = (i + " " + values[i]);
            }
            else{
                if (i < 10)
                    copy[i] = ("00" + i + " " + values[i]);
                else if (i < 100)
                    copy[i] = ("0" + i + " " + values[i]);
                else
                    copy[i] = (i + " " + values[i]);
            }
        }
        
        ComboBoxModel m = new DefaultComboBoxModel(copy);
        cb.setModel(m);
    }
    
    private void setComboUnits(int index){
        comboUnits.setSelectedIndex(index);
    }
    
    private int getComboUnits(){
        return comboUnits.getSelectedIndex();
    }
        
    private void setComboSeries(int index){
        comboSeries.setSelectedIndex(index);
    }
    
    private int getComboSeries(){
        return comboSeries.getSelectedIndex();
    }
    
    private void setComboItem(int index){
        comboItem.setSelectedIndex(index);
    }
    
    private int getComboItem(){
        return comboItem.getSelectedIndex();
    }
    
    private void setComboBGM(int index){
        comboBGM.setSelectedIndex(index);
    }
    
    private int getComboBGM(){
        return comboBGM.getSelectedIndex();
    }
    
    private void setComboCapture(int index){
        if (index > 1){
            index = 1;
            System.out.println("Unit - setComboCapture - unexpected capture value for unit " + getComboUnits());
        }
        
        comboCapture.setSelectedIndex(index);
    }
    
    private int getComboCapture(){
        return comboCapture.getSelectedIndex();
    }
    
    private void setComboSize(int index){
        comboSize.setSelectedIndex(index);
    }
    
    private int getComboSize(){
        return comboSize.getSelectedIndex();
    }
    
    private void setComboParts(int index){
        comboParts.setSelectedIndex(index);
    }
    
    private int getComboParts(){
        return comboParts.getSelectedIndex();
    }
    
    private void setComboMove(int index){
        comboMove.setSelectedIndex(index);
    }
    
    private int getComboMove(){
        return comboMove.getSelectedIndex();
    }
    
    public void setTerrain(byte[] t){
        if (t.length != 2)
            System.out.println( "Unit - setTerrain - wrong length: " + t.length );
        else{
            int value;
            
            // Space
            value = ( t[0] & 0xf0 ) >> 4;  // First nibble: Sx xx
            if (value > 4)
                System.out.println( "Unit - setTerrain - unexpected Space rating for unit " + getComboUnits() );
            else
                setComboSpace(value);
            
            // Water
            value = t[0] & 0x0f;  // Second nibble: xW xx
            if (value > 4)
                System.out.println( "Unit - setTerrain - unexpected Space rating for unit " + getComboUnits() );
            else
                setComboWater(value);
            
            // Land
            value = ( t[1] & 0xf0 ) >> 4;  // Thrid nibble: xx Lx
            if (value > 4)
                System.out.println( "Unit - setTerrain - unexpected Space rating for unit " + getComboUnits() );
            else
                setComboLand(value);
            
            // Air
            value = t[1] & 0x0f;  // Fourth nibble: xx xA
            if (value > 4)
                System.out.println( "Unit - setTerrain - unexpected Space rating for unit " + getComboUnits() );
            else
                setComboAir(value);
        }
    }
    
    public byte[] getTerrain(){
        byte[] b = new byte[2];
        Integer value;
        
        // Space
        value = getComboSpace();
        value = value << 4;
        b[0] = value.byteValue();  // First nibble: Sx xx
        
        // Water
        value = getComboWater();
        b[0] |= value.byteValue();  // Second nibble: xW xx
        
        // Land
        value = getComboLand();
        value = value << 4;
        b[1] = value.byteValue();  // Thrid nibble: xx Lx
        
        // Air
        value = getComboAir();
        b[1] |= value.byteValue();  // Fourth nibble: xx xA
                
        return b;
    }
    
    private void setComboSpace(int index){
        comboSpace.setSelectedIndex(index);
    }
    
    private int getComboSpace(){
        return comboSpace.getSelectedIndex();
    }
    
    private void setComboWater(int index){
        comboWater.setSelectedIndex(index);
    }
    
    private int getComboWater(){
        return comboWater.getSelectedIndex();
    }
    
    private void setComboLand(int index){
        comboLand.setSelectedIndex(index);
    }
    
    private int getComboLand(){
        return comboLand.getSelectedIndex();
    }
    
    private void setComboAir(int index){
        comboAir.setSelectedIndex(index);
    }
    
    private int getComboAir(){
        return comboAir.getSelectedIndex();
    }
    
    private void setComboUpgradesHP(int index){
        comboUpgradesHP.setSelectedIndex(index);
        setBodyMax();
        setHeadMax();
        setArmsMax();
        setLegsMax();
    }
    
    private int getComboUpgradesHP(){
        return comboUpgradesHP.getSelectedIndex();
    }
    
    private void setComboUpgradesEN(int index){
        comboUpgradesEN.setSelectedIndex(index);
        setEnergyMax();
    }
    
    private int getComboUpgradesEN(){
        return comboUpgradesEN.getSelectedIndex();
    }
    
    private void setComboUpgradesMob(int index){
        comboUpgradesMob.setSelectedIndex(index);
        setMobilityMax();
    }
    
    private int getComboUpgradesMob(){
        return comboUpgradesMob.getSelectedIndex();
    }
    
    private void setComboUpgradesArmor(int index){
        comboUpgradesArmor.setSelectedIndex(index);
        setArmorMax();
    }
    
    private int getComboUpgradesArmor(){
        return comboUpgradesArmor.getSelectedIndex();
    }
    
    private void setComboUpgradesWeapons(int index){
        comboUpgradesWeapons.setSelectedIndex(index);
        setUpgradesWeapons();   // Make sure to have added the weapons first!!
    }
    
    private int getComboUpgradesWeapons(){
        return comboUpgradesWeapons.getSelectedIndex();
    }
    
    private void setComboBody(int index){
        comboBody.setSelectedIndex(index);
	setBody();
    }
    
    private int getComboBody(){
        return comboBody.getSelectedIndex();
    }
    
    private void setComboHead(int index){
        comboHead.setSelectedIndex(index);
	setHead();
    }
    
    private int getComboHead(){
        return comboHead.getSelectedIndex();
    }
    
    private void setComboArms(int index){
        comboArms.setSelectedIndex(index);
	setArms();
    }
    
    private int getComboArms(){
        return comboArms.getSelectedIndex();
    }
    
    private void setComboLegs(int index){
        comboLegs.setSelectedIndex(index);
	setLegs();
    }
    
    private int getComboLegs(){
        return comboLegs.getSelectedIndex();
    }
    
    
    // --------------------------- Character Tab ---------------------------
    
    
    private void setComboChars(int index){
        comboChars.setSelectedIndex(index);
    }
    
    private int getComboChars(){
        return comboChars.getSelectedIndex();
    }
    
    private void setComboSeriesChar(int index){
        comboSeriesChar.setSelectedIndex(index);
    }
    
    private int getComboSeriesChar(){
        return comboSeriesChar.getSelectedIndex();
    }
    
    private void setComboSkillAces(int index){
        comboSkillAces.setSelectedIndex(index);
    }
    
    private int getComboSkillAces(){
        return comboSkillAces.getSelectedIndex();
    }
    
    private void setComboCommand1(int index){
        comboCommand1.setSelectedIndex(index);
    }
    
    private int getComboCommand1(){
        return comboCommand1.getSelectedIndex();
    }
    
    private void setComboCommand2(int index){
        comboCommand2.setSelectedIndex(index);
    }
    
    private int getComboCommand2(){
        return comboCommand2.getSelectedIndex();
    }
    
    private void setComboCommand3(int index){
        comboCommand3.setSelectedIndex(index);
    }
    
    private int getComboCommand3(){
        return comboCommand3.getSelectedIndex();
    }
    
    private void setComboCommand4(int index){
        comboCommand4.setSelectedIndex(index);
    }
    
    private int getComboCommand4(){
        return comboCommand4.getSelectedIndex();
    }
    
    private void setComboCommand5(int index){
        comboCommand5.setSelectedIndex(index);
    }
    
    private int getComboCommand5(){
        return comboCommand5.getSelectedIndex();
    }
    
    private void setComboCommand6(int index){
        comboCommand6.setSelectedIndex(index);
    }
    
    private int getComboCommand6(){
        return comboCommand6.getSelectedIndex();
    }
    
    private void setComboAlly(int index){
        comboAlly.setSelectedIndex(index);
    }
    
    private int getComboAlly(){
        return comboAlly.getSelectedIndex();
    }
    
    private void setComboSkillParts(int index){
        comboSkillParts.setSelectedIndex(index);
    }
    
    private int getComboSkillParts(){
        return comboSkillParts.getSelectedIndex();
    }
    
    private void setComboPersonality(int index){
        comboPersonality.setSelectedIndex(index);
        setPersonalityInfo();
    }
    
    private int getComboPersonality(){
        return comboPersonality.getSelectedIndex();
    }
    
    private void setComboGrowthSchema(int index){
        comboGrowthSchema.setSelectedIndex(index);
        setMaxStats();
    }
    
    private int getComboGrowthSchema(){
        return comboGrowthSchema.getSelectedIndex();
    }
    
    
    // -----------------------------------------------------------------
    // ------------------------- GET & SET for TextFields
    
    
    // --------------------------- Units Tab ---------------------------
    
    
    private void setReward(int value){
        fieldReward.setText("" + value);
        setSellValue();
    }
    
    private int getReward(){
        return Integer.valueOf( fieldReward.getText() );
        //return Short.valueOf(fieldReward.getText());
    }
    
    private void setEssential(int value){
        fieldEssential.setText("" + (value & 0xff) );   // Avoid negative values
        
        //if (value != 0 && (value & 0xff) != 255)
        //    System.out.println("Unit: " + current_unit + " - Unusual Essential value");
    }
    
    private int getEssential(){
        return Integer.valueOf( fieldEssential.getText() );
    }
    
    private void setRepair(int value){
        fieldRepair.setText("" + value);
    }
    
    private int getRepair(){
        return Integer.valueOf( fieldRepair.getText() );
        //return Short.valueOf(fieldRepair.getText());
    }
    
    private void setBodyBase(int value){
        fieldBodyBase.setText("" + value);
	setBodyMax();
    }
    
    private int getBodyBase(){
        return Integer.valueOf( fieldBodyBase.getText() );
    }
    
    private void setHeadBase(int value){
        fieldHeadBase.setText("" + value);
	setHeadMax();
    }
    
    private int getHeadBase(){
        return Integer.valueOf( fieldHeadBase.getText() );
    }
    
    private void setArmsBase(int value){
        fieldArmsBase.setText("" + value);
	setArmsMax();
    }
    
    private int getArmsBase(){
        return Integer.valueOf( fieldArmsBase.getText() );
    }
    
    private void setLegsBase(int value){
        fieldLegsBase.setText("" + value);
		setLegsMax();
    }
    
    private int getLegsBase(){
        return Integer.valueOf( fieldLegsBase.getText() );
    }
    
    private void setEnergyBase(int value){
        fieldEnergyBase.setText("" + value);
	setEnergyMax();
    }
    
    private int getEnergyBase(){
        return Integer.valueOf( fieldEnergyBase.getText() );
    }
    
    private void setMobilityBase(int value){
        fieldMobilityBase.setText("" + value);
	setMobilityMax();
    }
    
    private int getMobilityBase(){
        return Integer.valueOf( fieldMobilityBase.getText() );
    }
    
    private void setArmorBase(int value){
        fieldArmorBase.setText("" + value);
	setArmorMax();
    }
    
    private int getArmorBase(){
        return Integer.valueOf( fieldArmorBase.getText() );
    }
    
    private void setAI(int value){
        fieldAI.setText("" + (value & 0xff));   // Avoid negative values
    }
    
    private int getAI(){
        return Integer.valueOf( fieldAI.getText() );
    }
    
    private void setInflation(int value){
        fieldInflation.setText("" + value);
        setInflIncrease();
    }
    
    private int getInflation(){
        return Integer.valueOf( fieldInflation.getText() );
    }
    
    private void setLibID(int value){
        fieldLibID.setText("" + value);
    }
    
    private int getLibID(){
        return Integer.valueOf( fieldLibID.getText() );
    }
    
    private void setModelID(int value){
        fieldModelID.setText("" + value);
    }
    
    private int getModelID(){
        return Integer.valueOf( fieldModelID.getText() );
    }
    
    private void setByte37(int value){
        fieldByte37.setText("" + (value & 0xff) );   // Avoid negative values
        
        //if (value != 0)
        //    System.out.println("Unit: " + current_unit + " - Byte 37 not 0!");
    }
    
    private int getByte37(){
        return Integer.valueOf( fieldByte37.getText() );
    }
    
    private void setByte38(int value){
        fieldByte38.setText("" + (value & 0xff) );   // Avoid negative values
        
        //if (value != 0)
        //    System.out.println("Unit: " + current_unit + " - Byte 38 not 0!");
    }
    
    private int getByte38(){
        return Integer.valueOf( fieldByte38.getText() );
    }
    
    private void setByte39(int value){
        fieldByte39.setText("" + (value & 0xff) );   // Avoid negative values
        
        //if (value != 0)
        //    System.out.println("Unit: " + current_unit + " - Byte 39 not 0!");
    }
    
    private int getByte39(){
        return Integer.valueOf( fieldByte39.getText() );
    }
    
    private void setByte56(int value){
        fieldByte56.setText("" + (value & 0xff) );   // Avoid negative values
        
        //if (value != 0)
        //    System.out.println("Unit: " + current_unit + " - Byte 56 not 0!");
    }
    
    private int getByte56(){
        return Integer.valueOf( fieldByte56.getText() );
    }
    
    private void setByte60(int value){
        fieldByte60.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByte60(){
        return Integer.valueOf( fieldByte60.getText() );
    }
    
    private void setByte61(int value){
        fieldByte61.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByte61(){
        return Integer.valueOf( fieldByte61.getText() );
    }
    
    private void setByte62(int value){
        fieldByte62.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByte62(){
        return Integer.valueOf( fieldByte62.getText() );
    }
    
    private void setByte69(int value){
        fieldByte69.setText("" + (value & 0xff) );   // Avoid negative values
        
        //if (value != 0)
        //    System.out.println("Unit: " + current_unit + " - Byte 69 not 0!");
    }
    
    private int getByte69(){
        return Integer.valueOf( fieldByte69.getText() );
    }
        
    private void setByte71(int value){
        fieldByte71.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByte71(){
        return Integer.valueOf( fieldByte71.getText() );
    }
    
    private void setByte72(int value){
        fieldByte72.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByte72(){
        return Integer.valueOf( fieldByte72.getText() );
    }
    
    
    // --------------------------- Character Tab ---------------------------
    
    
    private void setLibIDChar(short value){
        fieldLibIDChar.setText("" + value );
    }
    
    private int getLibIDChar(){
        return Integer.valueOf( fieldLibIDChar.getText() );
    }
    
    private void setPortrait(int value){
        fieldPortrait.setText("" + value);
    }
    
    private int getPortrait(){
        return Integer.valueOf( fieldPortrait.getText() );
    }
    
    private void setEnemyAI(int value){
        fieldEnemyAI.setText("" + (value & 0xff));  // Avoid negative values
    }
    
    private int getEnemyAI(){
        return Integer.valueOf( fieldEnemyAI.getText() );
    }
    
    private void setMeleeBase(int value){
        fieldMeleeBase.setText("" + (value & 0xff));
        setMeleeMax();
    }
    
    private int getMeleeBase(){
        return Integer.valueOf( fieldMeleeBase.getText() );
    }
    
    private void setRangedBase(int value){
        fieldRangedBase.setText("" + (value & 0xff));
        setRangedMax();
    }
    
    private int getRangedBase(){
        return Integer.valueOf( fieldRangedBase.getText() );
    }
    
    private void setDefenseBase(int value){
        fieldDefenseBase.setText("" + (value & 0xff));
        setDefenseMax();
    }
    
    private int getDefenseBase(){
        return Integer.valueOf( fieldDefenseBase.getText() );
    }
    
    private void setSkillBase(int value){
        fieldSkillBase.setText("" + (value & 0xff));
        setSkillMax();
    }
    
    private int getSkillBase(){
        return Integer.valueOf( fieldSkillBase.getText() );
    }
    
    private void setAccuracyBase(int value){
        fieldAccuracyBase.setText("" + (value & 0xff));
        setAccuracyMax();
    }
    
    private int getAccuracyBase(){
        return Integer.valueOf( fieldAccuracyBase.getText() );
    }
    
    private void setEvasionBase(int value){
        fieldEvasionBase.setText("" + (value & 0xff));
        setEvasionMax();
    }
    
    private int getEvasionBase(){
        return Integer.valueOf( fieldEvasionBase.getText() );
    }
    
    private void setSPBase(int value){
        fieldSPBase.setText("" + value);
        setSPMax();
    }
    
    private int getSPBase(){
        return Integer.valueOf( fieldSPBase.getText() );
    }
    
    private void setCostCommand1(int value){
        fieldCostCommand1.setText("" + value);
    }
    
    private int getCostCommand1(){
        return Integer.valueOf( fieldCostCommand1.getText() );
    }
    
    private void setLearnCommand1(int value){
        fieldLearnCommand1.setText("" + value);
    }
    
    private int getLearnCommand1(){
        return Integer.valueOf( fieldLearnCommand1.getText() );
    }
    
    private void setCostCommand2(int value){
        fieldCostCommand2.setText("" + value);
    }
    
    private int getCostCommand2(){
        return Integer.valueOf( fieldCostCommand2.getText() );
    }
    
    private void setLearnCommand2(int value){
        fieldLearnCommand2.setText("" + value);
    }
    
    private int getLearnCommand2(){
        return Integer.valueOf( fieldLearnCommand2.getText() );
    }
    
    private void setCostCommand3(int value){
        fieldCostCommand3.setText("" + value);
    }
    
    private int getCostCommand3(){
        return Integer.valueOf( fieldCostCommand3.getText() );
    }
    
    private void setLearnCommand3(int value){
        fieldLearnCommand3.setText("" + value);
    }
    
    private int getLearnCommand3(){
        return Integer.valueOf( fieldLearnCommand3.getText() );
    }
    
    private void setCostCommand4(int value){
        fieldCostCommand4.setText("" + value);
    }
    
    private int getCostCommand4(){
        return Integer.valueOf( fieldCostCommand4.getText() );
    }
    
    private void setLearnCommand4(int value){
        fieldLearnCommand4.setText("" + value);
    }
    
    private int getLearnCommand4(){
        return Integer.valueOf( fieldLearnCommand4.getText() );
    }
    
    private void setCostCommand5(int value){
        fieldCostCommand5.setText("" + value);
    }
    
    private int getCostCommand5(){
        return Integer.valueOf( fieldCostCommand5.getText() );
    }
    
    private void setLearnCommand5(int value){
        fieldLearnCommand5.setText("" + value);
    }
    
    private int getLearnCommand5(){
        return Integer.valueOf( fieldLearnCommand5.getText() );
    }
    
    private void setCostCommand6(int value){
        fieldCostCommand6.setText("" + value);
    }
    
    private int getCostCommand6(){
        return Integer.valueOf( fieldCostCommand6.getText() );
    }
    
    private void setLearnCommand6(int value){
        fieldLearnCommand6.setText("" + value);
    }
    
    private int getLearnCommand6(){
        return Integer.valueOf( fieldLearnCommand6.getText() );
    }
    
    private void setNTlv1(int value){
        fieldNTlv1.setText("" + value);
    }
    
    private int getNTlv1(){
        return Integer.valueOf( fieldNTlv1.getText() );
    }
    
    private void setNTlv2(int value){
        fieldNTlv2.setText("" + value);
    }
    
    private int getNTlv2(){
        return Integer.valueOf( fieldNTlv2.getText() );
    }
    
    private void setNTlv3(int value){
        fieldNTlv3.setText("" + value);
    }
    
    private int getNTlv3(){
        return Integer.valueOf( fieldNTlv3.getText() );
    }
    
    private void setNTlv4(int value){
        fieldNTlv4.setText("" + value);
    }
    
    private int getNTlv4(){
        return Integer.valueOf( fieldNTlv4.getText() );
    }
    
    private void setNTlv5(int value){
        fieldNTlv5.setText("" + value);
    }
    
    private int getNTlv5(){
        return Integer.valueOf( fieldNTlv5.getText() );
    }
    
    private void setNTlv6(int value){
        fieldNTlv6.setText("" + value);
    }
    
    private int getNTlv6(){
        return Integer.valueOf( fieldNTlv6.getText() );
    }
    
    private void setNTlv7(int value){
        fieldNTlv7.setText("" + value);
    }
    
    private int getNTlv7(){
        return Integer.valueOf( fieldNTlv7.getText() );
    }
    
    private void setNTlv8(int value){
        fieldNTlv8.setText("" + value);
    }
    
    private int getNTlv8(){
        return Integer.valueOf( fieldNTlv8.getText() );
    }
    
    private void setNTlv9(int value){
        fieldNTlv9.setText("" + value);
    }
    
    private int getNTlv9(){
        return Integer.valueOf( fieldNTlv9.getText() );
    }
    
    private void setPotentialLv1(int value){
        fieldPotentialLv1.setText("" + value);
    }
    
    private int getPotentialLv1(){
        return Integer.valueOf( fieldPotentialLv1.getText() );
    }
    
    private void setPotentialLv2(int value){
        fieldPotentialLv2.setText("" + value);
    }
    
    private int getPotentialLv2(){
        return Integer.valueOf( fieldPotentialLv2.getText() );
    }
    
    private void setPotentialLv3(int value){
        fieldPotentialLv3.setText("" + value);
    }
    
    private int getPotentialLv3(){
        return Integer.valueOf( fieldPotentialLv3.getText() );
    }
    
    private void setPotentialLv4(int value){
        fieldPotentialLv4.setText("" + value);
    }
    
    private int getPotentialLv4(){
        return Integer.valueOf( fieldPotentialLv4.getText() );
    }
    
    private void setPotentialLv5(int value){
        fieldPotentialLv5.setText("" + value);
    }
    
    private int getPotentialLv5(){
        return Integer.valueOf( fieldPotentialLv5.getText() );
    }
    
    private void setPotentialLv6(int value){
        fieldPotentialLv6.setText("" + value);
    }
    
    private int getPotentialLv6(){
        return Integer.valueOf( fieldPotentialLv6.getText() );
    }
    
    private void setPotentialLv7(int value){
        fieldPotentialLv7.setText("" + value);
    }
    
    private int getPotentialLv7(){
        return Integer.valueOf( fieldPotentialLv7.getText() );
    }
    
    private void setPotentialLv8(int value){
        fieldPotentialLv8.setText("" + value);
    }
    
    private int getPotentialLv8(){
        return Integer.valueOf( fieldPotentialLv8.getText() );
    }
    
    private void setPotentialLv9(int value){
        fieldPotentialLv9.setText("" + value);
    }
    
    private int getPotentialLv9(){
        return Integer.valueOf( fieldPotentialLv9.getText() );
    }
    
    private void setSupportLv1(int value){
        fieldSupportLv1.setText("" + value);
    }
    
    private int getSupportLv1(){
        return Integer.valueOf( fieldSupportLv1.getText() );
    }
    
    private void setSupportLv2(int value){
        fieldSupportLv2.setText("" + value);
    }
    
    private int getSupportLv2(){
        return Integer.valueOf( fieldSupportLv2.getText() );
    }
    
    private void setSupportLv3(int value){
        fieldSupportLv3.setText("" + value);
    }
    
    private int getSupportLv3(){
        return Integer.valueOf( fieldSupportLv3.getText() );
    }
    
    private void setSupportLv4(int value){
        fieldSupportLv4.setText("" + value);
    }
    
    private int getSupportLv4(){
        return Integer.valueOf( fieldSupportLv4.getText() );
    }
    
    private void setCommandLv1(int value){
        fieldCommandLv1.setText("" + value);
    }
    
    private int getCommandLv1(){
        return Integer.valueOf( fieldCommandLv1.getText() );
    }
    
    private void setCommandLv2(int value){
        fieldCommandLv2.setText("" + value);
    }
    
    private int getCommandLv2(){
        return Integer.valueOf( fieldCommandLv2.getText() );
    }
    
    private void setCommandLv3(int value){
        fieldCommandLv3.setText("" + value);
    }
    
    private int getCommandLv3(){
        return Integer.valueOf( fieldCommandLv3.getText() );
    }
    
    private void setCommandLv4(int value){
        fieldCommandLv4.setText("" + value);
    }
    
    private int getCommandLv4(){
        return Integer.valueOf( fieldCommandLv4.getText() );
    }
    
    private void setByteChar70(int value){
        fieldByteChar70.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar70(){
        return Integer.valueOf( fieldByteChar70.getText() );
    }
    
    private void setByteChar71(int value){
        fieldByteChar71.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar71(){
        return Integer.valueOf( fieldByteChar71.getText() );
    }
    
    private void setByteChar72(int value){
        fieldByteChar72.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar72(){
        return Integer.valueOf( fieldByteChar72.getText() );
    }
    
    private void setByteChar79(int value){
        fieldByteChar79.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar79(){
        return Integer.valueOf( fieldByteChar79.getText() );
    }
    
    private void setByteChar80(int value){
        fieldByteChar80.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar80(){
        return Integer.valueOf( fieldByteChar80.getText() );
    }
    
    private void setByteChar81(int value){
        fieldByteChar81.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar81(){
        return Integer.valueOf( fieldByteChar81.getText() );
    }
    
    private void setByteChar82(int value){
        fieldByteChar82.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar82(){
        return Integer.valueOf( fieldByteChar82.getText() );
    }
    
    private void setByteChar83(int value){
        fieldByteChar83.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar83(){
        return Integer.valueOf( fieldByteChar83.getText() );
    }
    
    private void setByteChar84(int value){
        fieldByteChar84.setText("" + (value & 0xff) );   // Avoid negative values
    }
    
    private int getByteChar84(){
        return Integer.valueOf( fieldByteChar84.getText() );
    }
    
        
    // -----------------------------------------------------------------
    // ------------------------- GET & SET for CheckBoxes
    
    
    // --------------------------- Units Tab ---------------------------
    
    private void setBuilding(byte b){
        int value;
        
        value = b & 0x01;
        setCheckBuilding(value == 1);
        
        if (b > 1)
            System.out.println( "Unit - Unexpected Building value for unit " + getComboUnits() );        
    }
    
    private byte getBuilding(){
        byte b = 0;
        
        if (getCheckBuilding())
            b = 1;
        
        return b;
    }
    
    private void setCheckBuilding(boolean s){
        checkBuilding.setSelected(s);
    }
    
    private boolean getCheckBuilding(){
        return checkBuilding.isSelected();
    }
    
    private void setTerrainType(byte t){
        int value;
        
        // Air ---- ---X
        value = t & 0x01;        
        setCheckAir(value == 1);
        
        // Land ---- --X-
        value = t & 0x02;        
        setCheckLand(value == 2);
        
        // Water ---- -X--
        value = t & 0x04;        
        setCheckWater(value == 4);
        
        // Ground ---- X---
        value = t & 0x08;        
        setCheckGround(value == 8);
        
        // Hover ---X ----
        value = t & 0x10;        
        setCheckHover(value == 16);
        
        // ??? --X- ----
        value = t & 0x20;        
        if (value == 32)
            System.out.println( "Unit - Unexpected Terrain Type for unit " + getComboUnits() );
        
        // ??? -X-- ----
        value = t & 0x40;       
        if (value == 64)
            System.out.println( "Unit - Unexpected Terrain Type for unit " + getComboUnits() );
        
        // ??? X--- ----
        value = t & 0x80;      
        if (value == 128)
            System.out.println( "Unit - Unexpected Terrain Type for unit " + getComboUnits() );
    }
    
    public byte getTerrainType(){
        byte b = 0;
        
        // Air ---- ---X
        if( getCheckAir() )
            b |= 0x01;
        
        // Land ---- --X-
        if( getCheckLand() )
            b |= 0x02;
        
        // Water ---- -X--
        if( getCheckWater() )
            b |= 0x04;
        
        // Ground ---- X---
        if( getCheckGround() )
            b |= 0x08;
        
        // Hover ---X ----
        if( getCheckHover() )
            b |= 0x10;
        
        // The other bits are supposed to be empty all the time
        
        return b;
    }
    
    private void setCheckAir(boolean s){
        checkAir.setSelected(s);
    }
    
    private boolean getCheckAir(){
        return checkAir.isSelected();
    }
    
    private void setCheckLand(boolean s){
        checkLand.setSelected(s);
    }
    
    private boolean getCheckLand(){
        return checkLand.isSelected();
    }
    
    private void setCheckWater(boolean s){
        checkWater.setSelected(s);
    }
    
    private boolean getCheckWater(){
        return checkWater.isSelected();
    }
    
    private void setCheckGround(boolean s){
        checkGround.setSelected(s);
    }
    
    private boolean getCheckGround(){
        return checkGround.isSelected();
    }
    
    private void setCheckHover(boolean s){
        checkHover.setSelected(s);
    }
    
    private boolean getCheckHover(){
        return checkHover.isSelected();
    }
    
    private void setShield(byte s){
        int value;
        
        value = s & 0x01;
        setCheckShield(value == 1);
        
        if (s > 1)
            System.out.println( "Unit - Unexpected Shield value for unit " + getComboUnits() );        
    }
    
    private byte getShield(){
        byte b = 0;
        
        if (getCheckShield())
            b = 1;
        
        return b;
    }
    
    private void setCheckShield(boolean s){
        checkShield.setSelected(s);
    }
    
    private boolean getCheckShield(){
        return checkShield.isSelected();
    }
    
    public void setFamUnit(byte[] f){
        int value;
        
        // Family values come in 2 bytes and are read in reverse
        // First byte to be read is the 2nd one, and we go right to left

        // Family values in 2nd byte
		
        // Gundam ---- ---X
        value = f[1] & 0x01;        
        setFamUnit(1, value == 1);
        
        // L-Gaim ---- --X-
        value = f[1] & 0x02;        
        setFamUnit(2, value == 2);
        
        // Layzner ---- -X--
        value = f[1] & 0x04;        
        setFamUnit(3, value == 4);
        
        // Dragonar ---- X---
        value = f[1] & 0x08;        
        setFamUnit(4, value == 8);
        
        // Mazinger ---X ----
        value = f[1] & 0x10;        
        setFamUnit(5, value == 16);
        
        // Getter --X- ----
        value = f[1] & 0x20;        
        setFamUnit(6, value == 32);
        
        // RaijinOh -X-- ----
        value = f[1] & 0x40;        
        setFamUnit(7, value == 64);
        
        // Eiji X--- ----
        value = f[1] & 0x80;        
        setFamUnit(8, value == 128);     
		
        // Family values in 1st byte
		
        // Kaine ---- ---X
        value = f[0] & 0x01;        
        setFamUnit(9, value == 1);
        
        // Tapp ---- --X-
        value = f[0] & 0x02;        
        setFamUnit(10, value == 2);
        
        // Light ---- -X--
        value = f[0] & 0x04;        
        setFamUnit(11, value == 4);
        
        // Kouji (Z) ---- X---
        value = f[0] & 0x08;        
        setFamUnit(12, value == 8);
        
        // Lilith (sub) ---X ----
        value = f[0] & 0x10;        
        setFamUnit(13, value == 16);
        
        // Unused #1 --X- ----
        value = f[0] & 0x20;        
        setFamUnit(14, value == 32);
        
        // Unused #2 -X-- ----
        value = f[0] & 0x40;        
        setFamUnit(15, value == 64);
        
        // Unused #3 X--- ----
        value = f[0] & 0x80;        
        setFamUnit(16, value == 128);    
    }
    
    public byte[] getFamUnit(){
        byte[] b = new byte[2];
        
	// Family values in 2nd byte
		
        // Gundam ---- ---X
        if( getFamUnit(1) )
            b[1] |= 0x01;
        
        // L-Gaim ---- --X-
        if( getFamUnit(2) )
            b[1] |= 0x02;
        
        // Layzner ---- -X--
        if( getFamUnit(3) )
            b[1] |= 0x04;
        
        // Dragonar ---- X---
        if( getFamUnit(4) )
            b[1] |= 0x08;
        
        // Mazinger ---X ----
        if( getFamUnit(5) )
            b[1] |= 0x10;
        
        // Getter --X- ----
        if( getFamUnit(6) )
            b[1] |= 0x20;
        
        // RaijinOh -X-- ----
        if( getFamUnit(7) )
            b[1] |= 0x40;
        
        // Eiji X--- ----
        if( getFamUnit(8) )
            b[1] |= 0x80; 
		
	// Family values in 1st byte
		
        // Kaine ---- ---X
        if( getFamUnit(9) )
            b[0] |= 0x01;
        
        // Tapp ---- --X-
        if( getFamUnit(10) )
            b[0] |= 0x02;
        
        // Light ---- -X--
        if( getFamUnit(11) )
            b[0] |= 0x04;
        
        // Kouji (Z) ---- X---
        if( getFamUnit(12) )
            b[0] |= 0x08;
        
        // Lilith (sub) ---X ----
        if( getFamUnit(13) )
            b[0] |= 0x10;
        
        // Unused #1 --X- ----
        if( getFamUnit(14) )
            b[0] |= 0x20;
        
        // Unused #2 -X-- ----
        if( getFamUnit(15) )
            b[0] |= 0x40;
        
        // Unused #3 X--- ----
        if( getFamUnit(16) )
            b[0] |= 0x80;  
        
        return b;
    }
    
    private void setFamUnit(int id, boolean active){
        switch(id){
            case 1:
                checkFamUnit01.setSelected(active);
                break;
            case 2:
                checkFamUnit02.setSelected(active);
                break;
            case 3:
                checkFamUnit03.setSelected(active);
                break;
            case 4:
                checkFamUnit04.setSelected(active);
                break;
            case 5:
                checkFamUnit05.setSelected(active);
                break;
            case 6:
                checkFamUnit06.setSelected(active);
                break;
            case 7:
                checkFamUnit07.setSelected(active);
                break;
            case 8:
                checkFamUnit08.setSelected(active); 
                break;    
            case 9:
                checkFamUnit09.setSelected(active);
                break;
            case 10:
                checkFamUnit10.setSelected(active);
                break;
            case 11:
                checkFamUnit11.setSelected(active);
                break;
            case 12:
                checkFamUnit12.setSelected(active);
                break;
            case 13:
                checkFamUnit13.setSelected(active);
                break;
            case 14:
                checkFamUnit14.setSelected(active);
                break;
            case 15:
                checkFamUnit15.setSelected(active);
                break;
            case 16:
                checkFamUnit16.setSelected(active); 
                break;    
            default:
                System.out.println("Unit: setFamUnit - wrong ID " + id);
                break;
        }
    }
    
    private boolean getFamUnit(int id){
        switch(id){
            case 1:
                return checkFamUnit01.isSelected();
            case 2:
                return checkFamUnit02.isSelected();
            case 3:
                return checkFamUnit03.isSelected();
            case 4:
                return checkFamUnit04.isSelected();
            case 5:
                return checkFamUnit05.isSelected();
            case 6:
                return checkFamUnit06.isSelected();
            case 7:
                return checkFamUnit07.isSelected();
            case 8:
                return checkFamUnit08.isSelected();
            case 9:
                return checkFamUnit09.isSelected();
            case 10:
                return checkFamUnit10.isSelected();
            case 11:
                return checkFamUnit11.isSelected();
            case 12:
                return checkFamUnit12.isSelected();
            case 13:
                return checkFamUnit13.isSelected();
            case 14:
                return checkFamUnit14.isSelected();
            case 15:
                return checkFamUnit15.isSelected();
            case 16:
                return checkFamUnit16.isSelected();
            default:
                System.out.println("Unit: getFamUnit - wrong ID " + id);
                return false;
        }
    }
    
    public void setAbil(byte[] a){
        int value;
        
        // Abilities come in 4 bytes and are read in reverse
        // First byte to be read is the 4th one, and we go right to left

        // Abilities in 4th byte
		
        // Transform ---- ---X
        value = a[3] & 0x01;        
        setAbil(1, value == 1);
        
        // Combine ---- --X-
        value = a[3] & 0x02;        
        setAbil(2, value == 2);
        
        // Separate ---- -X--
        value = a[3] & 0x04;        
        setAbil(3, value == 4);
        
        // Repair unit ---- X---
        value = a[3] & 0x08;        
        setAbil(4, value == 8);
        
        // Supply unit ---X ----
        value = a[3] & 0x10;        
        setAbil(5, value == 16);
        
        // Boarding --X- ----
        value = a[3] & 0x20;        
        setAbil(6, value == 32);
        
        // Capture -X-- ----
        value = a[3] & 0x40;        
        setAbil(7, value == 64);
        
        // Double Image X--- ----
        value = a[3] & 0x80;        
        setAbil(8, value == 128);     
		
        // Abilities in 3rd byte
		
        // Neo Getter Vision ---- ---X
        value = a[2] & 0x01;        
        setAbil(9, value == 1);
        
        // Shin Mach Special ---- --X-
        value = a[2] & 0x02;        
        setAbil(10, value == 2);
        
        // Jammer ---- -X--
        value = a[2] & 0x04;        
        setAbil(11, value == 4);
        
        // Beam Coat S ---- X---
        value = a[2] & 0x08;        
        setAbil(12, value == 8);
        
        // Beam Coat M ---X ----
        value = a[2] & 0x10;        
        setAbil(13, value == 16);
        
        // Beam Coat L --X- ----
        value = a[2] & 0x20;        
        setAbil(14, value == 32);
        
        // I-Field -X-- ----
        value = a[2] & 0x40;        
        setAbil(15, value == 64);
        
        // HP Regen S X--- ----
        value = a[2] & 0x80;        
        setAbil(16, value == 128);    
		
        // Abilities in 2nd byte
		
        // HP Regen M ---- ---X
        value = a[1] & 0x01;        
        setAbil(17, value == 1);
        
        // HP Regen L ---- --X-
        value = a[1] & 0x02;        
        setAbil(18, value == 2);
        
        // EN Regen S ---- -X--
        value = a[1] & 0x04;        
        setAbil(19, value == 4);
        
        // EN Regen M ---- X---
        value = a[1] & 0x08;        
        setAbil(20, value == 8);
        
        // EN Regen L ---X ----
        value = a[1] & 0x10;        
        setAbil(21, value == 16);
        
        // Mazin Power --X- ----
        value = a[1] & 0x20;        
        setAbil(22, value == 32);
        
        // EWAC -X-- ----
        value = a[1] & 0x40;        
        setAbil(23, value == 64);
        
        // V-MAX X--- ----
        value = a[1] & 0x80;        
        setAbil(24, value == 128);    
		
        // Abilities in 1st byte
		
        // V-MAX Red Power ---- ---X
        value = a[0] & 0x01;        
        setAbil(25, value == 1);
        
        // V-MAXIMUM ---- --X-
        value = a[0] & 0x02;        
        setAbil(26, value == 2);
        
        // Unused #1 ---- -X--
        value = a[0] & 0x04;    
	if (value == 4)
            System.out.println("Unit: setAbilities - Unexpected value for ID " + getComboUnits());
        
        // Unused #2 ---- X---
        value = a[0] & 0x08;        
	if (value == 8)
            System.out.println("Unit: setAbilities - Unexpected value for ID " + getComboUnits());
        
        // Unused #3 ---X ----
        value = a[0] & 0x10;        
	if (value == 16)
            System.out.println("Unit: setAbilities - Unexpected value for ID " + getComboUnits());
        
        // Unused #4 --X- ----
        value = a[0] & 0x20;        
	if (value == 32)
            System.out.println("Unit: setAbilities - Unexpected value for ID " + getComboUnits());
        
        // Unused #5 -X-- ----
        value = a[0] & 0x40;       
	if (value == 64)
            System.out.println("Unit: setAbilities - Unexpected value for ID " + getComboUnits());
        
        // Unused #6 X--- ----
        value = a[0] & 0x80;        
        if (value == 128)
            System.out.println("Unit: setAbilities - Unexpected value for ID " + getComboUnits());
    }
    
    public byte[] getAbil(){
        byte[] b = new byte[4];
        
	// Abilities in 4th byte
		
        // Transform ---- ---X
        if( getAbil(1) )
            b[3] |= 0x01;
        
        // Instinct ---- --X-
        if( getAbil(2) )
            b[3] |= 0x02;
        
        // Separate ---- -X--
        if( getAbil(3) )
            b[3] |= 0x04;
        
        // Repair unit ---- X---
        if( getAbil(4) )
            b[3] |= 0x08;
        
        // Supply unit ---X ----
        if( getAbil(5) )
            b[3] |= 0x10;
        
        // Boarding --X- ----
        if( getAbil(6) )
            b[3] |= 0x20;
        
        // Capture -X-- ----
        if( getAbil(7) )
            b[3] |= 0x40;
        
        // Double Image X--- ----
        if( getAbil(8) )
            b[3] |= 0x80; 
		
	// Abilities in 3rd byte
		
        // Neo Getter Vision ---- ---X
        if( getAbil(9) )
            b[2] |= 0x01;
        
        // Shin Mach Special ---- --X-
        if( getAbil(10) )
            b[2] |= 0x02;
        
        // Jammer ---- -X--
        if( getAbil(11) )
            b[2] |= 0x04;
        
        // Beam Coat S ---- X---
        if( getAbil(12) )
            b[2] |= 0x08;
        
        // Beam Coat M ---X ----
        if( getAbil(13) )
            b[2] |= 0x10;
        
        // Beam Coat L --X- ----
        if( getAbil(14) )
            b[2] |= 0x20;
        
        // I-Field -X-- ----
        if( getAbil(15) )
            b[2] |= 0x40;
        
        // HP Regen S X--- ----
        if( getAbil(16) )
            b[2] |= 0x80;  
		
        // Abilities in 2nd byte
		
        // HP Regen M ---- ---X
        if( getAbil(17) )
            b[1] |= 0x01;
        
        // HP Regen L ---- --X-
        if( getAbil(18) )
            b[1] |= 0x02;
        
        // EN Regen S ---- -X--
        if( getAbil(19) )
            b[1] |= 0x04;
        
        // EN Regen M ---- X---
        if( getAbil(20) )
            b[1] |= 0x08;
        
        // EN Regen L ---X ----
        if( getAbil(21) )
            b[1] |= 0x10;
        
        // Mazin Power --X- ----
        if( getAbil(22) )
            b[1] |= 0x20;
        
        // EWAC -X-- ----
        if( getAbil(23) )
            b[1] |= 0x40;
        
        // V-MAX X--- ----
        if( getAbil(24) )
            b[1] |= 0x80;   
		
        // Abilities in 1st byte
		
        // V-MAX Red Power ---- ---X
        if( getAbil(25) )
            b[0] |= 0x01;
        
        // V-MAXIMUM ---- --X-
        if( getAbil(26) )
            b[0] |= 0x02;
        
        return b;
    }
    
    private void setAbil(int id, boolean active){
        switch(id){
            case 1:
                checkAbil01.setSelected(active);
                break;
            case 2:
                checkAbil02.setSelected(active);
                break;
            case 3:
                checkAbil03.setSelected(active);
                break;
            case 4:
                checkAbil04.setSelected(active);
                break;
            case 5:
                checkAbil05.setSelected(active);
                break;
            case 6:
                checkAbil06.setSelected(active);
                break;
            case 7:
                checkAbil07.setSelected(active);
                break;
            case 8:
                checkAbil08.setSelected(active); 
                break;    
            case 9:
                checkAbil09.setSelected(active);
                break;
            case 10:
                checkAbil10.setSelected(active);
                break;
            case 11:
                checkAbil11.setSelected(active);
                break;
            case 12:
                checkAbil12.setSelected(active);
                break;
            case 13:
                checkAbil13.setSelected(active);
                break;
            case 14:
                checkAbil14.setSelected(active);
                break;
            case 15:
                checkAbil15.setSelected(active);
                break;
            case 16:
                checkAbil16.setSelected(active); 
                break;    
            case 17:
                checkAbil17.setSelected(active);
                break;
            case 18:
                checkAbil18.setSelected(active); 
                break;    
            case 19:
                checkAbil19.setSelected(active);
                break;
            case 20:
                checkAbil20.setSelected(active);
                break;
            case 21:
                checkAbil21.setSelected(active);
                break;
            case 22:
                checkAbil22.setSelected(active);
                break;
            case 23:
                checkAbil23.setSelected(active);
                break;
            case 24:
                checkAbil24.setSelected(active);
                break;
            case 25:
                checkAbil25.setSelected(active);
                break;
            case 26:
                checkAbil26.setSelected(active); 
                break;   
            default:
                System.out.println("Unit: setAbil - wrong ID " + id);
                break;
        }
    }
    
    private boolean getAbil(int id){
        switch(id){
            case 1:
                return checkAbil01.isSelected();
            case 2:
                return checkAbil02.isSelected();
            case 3:
                return checkAbil03.isSelected();
            case 4:
                return checkAbil04.isSelected();
            case 5:
                return checkAbil05.isSelected();
            case 6:
                return checkAbil06.isSelected();
            case 7:
                return checkAbil07.isSelected();
            case 8:
                return checkAbil08.isSelected();
            case 9:
                return checkAbil09.isSelected();
            case 10:
                return checkAbil10.isSelected();
            case 11:
                return checkAbil11.isSelected();
            case 12:
                return checkAbil12.isSelected();
            case 13:
                return checkAbil13.isSelected();
            case 14:
                return checkAbil14.isSelected();
            case 15:
                return checkAbil15.isSelected();
            case 16:
                return checkAbil16.isSelected();
            case 17:
                return checkAbil17.isSelected();
            case 18:
                return checkAbil18.isSelected();
            case 19:
                return checkAbil19.isSelected();
            case 20:
                return checkAbil20.isSelected();
            case 21:
                return checkAbil21.isSelected();
            case 22:
                return checkAbil22.isSelected();
            case 23:
                return checkAbil23.isSelected();
            case 24:
                return checkAbil24.isSelected();
            case 25:
                return checkAbil25.isSelected();
            case 26:
                return checkAbil26.isSelected(); 
            default:
                System.out.println("Unit: getAbil - wrong ID " + id);
                return false;
        }
    }
    
    
    // --------------------------- Character Tab ---------------------------
    
    
    public void setFamChar(byte[] f){
        int value;
        
        // Family values come in 2 bytes and are read in reverse
        // First byte to be read is the 2nd one, and we go right to left

        // Family values in 2nd byte
		
        // Gundam ---- ---X
        value = f[1] & 0x01;        
        setFamChar(1, value == 1);
        
        // L-Gaim ---- --X-
        value = f[1] & 0x02;        
        setFamChar(2, value == 2);
        
        // Layzner ---- -X--
        value = f[1] & 0x04;        
        setFamChar(3, value == 4);
        
        // Dragonar ---- X---
        value = f[1] & 0x08;        
        setFamChar(4, value == 8);
        
        // Mazinger ---X ----
        value = f[1] & 0x10;        
        setFamChar(5, value == 16);
        
        // Getter --X- ----
        value = f[1] & 0x20;        
        setFamChar(6, value == 32);
        
        // RaijinOh -X-- ----
        value = f[1] & 0x40;        
        setFamChar(7, value == 64);
        
        // Eiji X--- ----
        value = f[1] & 0x80;        
        setFamChar(8, value == 128);     
		
        // Family values in 1st byte
		
        // Kaine ---- ---X
        value = f[0] & 0x01;        
        setFamChar(9, value == 1);
        
        // Tapp ---- --X-
        value = f[0] & 0x02;        
        setFamChar(10, value == 2);
        
        // Light ---- -X--
        value = f[0] & 0x04;        
        setFamChar(11, value == 4);
        
        // Kouji (Z) ---- X---
        value = f[0] & 0x08;        
        setFamChar(12, value == 8);
        
        // Lilith (sub) ---X ----
        value = f[0] & 0x10;        
        setFamChar(13, value == 16);
        
        // Unused #1 --X- ----
        value = f[0] & 0x20;        
        setFamChar(14, value == 32);
        
        // Unused #2 -X-- ----
        value = f[0] & 0x40;        
        setFamChar(15, value == 64);
        
        // Unused #3 X--- ----
        value = f[0] & 0x80;        
        setFamChar(16, value == 128);    
    }
    
    public byte[] getFamChar(){
        byte[] b = new byte[2];
        
	// Family values in 2nd byte
		
        // Gundam ---- ---X
        if( getFamChar(1) )
            b[1] |= 0x01;
        
        // L-Gaim ---- --X-
        if( getFamChar(2) )
            b[1] |= 0x02;
        
        // Layzner ---- -X--
        if( getFamChar(3) )
            b[1] |= 0x04;
        
        // Dragonar ---- X---
        if( getFamChar(4) )
            b[1] |= 0x08;
        
        // Mazinger ---X ----
        if( getFamChar(5) )
            b[1] |= 0x10;
        
        // Getter --X- ----
        if( getFamChar(6) )
            b[1] |= 0x20;
        
        // RaijinOh -X-- ----
        if( getFamChar(7) )
            b[1] |= 0x40;
        
        // Eiji X--- ----
        if( getFamChar(8) )
            b[1] |= 0x80; 
		
	// Family values in 1st byte
		
        // Kaine ---- ---X
        if( getFamChar(9) )
            b[0] |= 0x01;
        
        // Tapp ---- --X-
        if( getFamChar(10) )
            b[0] |= 0x02;
        
        // Light ---- -X--
        if( getFamChar(11) )
            b[0] |= 0x04;
        
        // Kouji (Z) ---- X---
        if( getFamChar(12) )
            b[0] |= 0x08;
        
        // Lilith (sub) ---X ----
        if( getFamChar(13) )
            b[0] |= 0x10;
        
        // Unused #1 --X- ----
        if( getFamChar(14) )
            b[0] |= 0x20;
        
        // Unused #2 -X-- ----
        if( getFamChar(15) )
            b[0] |= 0x40;
        
        // Unused #3 X--- ----
        if( getFamChar(16) )
            b[0] |= 0x80;  
        
        return b;
    }
    
    private void setFamChar(int id, boolean active){
        switch(id){
            case 1:
                checkFamChar01.setSelected(active);
                break;
            case 2:
                checkFamChar02.setSelected(active);
                break;
            case 3:
                checkFamChar03.setSelected(active);
                break;
            case 4:
                checkFamChar04.setSelected(active);
                break;
            case 5:
                checkFamChar05.setSelected(active);
                break;
            case 6:
                checkFamChar06.setSelected(active);
                break;
            case 7:
                checkFamChar07.setSelected(active);
                break;
            case 8:
                checkFamChar08.setSelected(active); 
                break;    
            case 9:
                checkFamChar09.setSelected(active);
                break;
            case 10:
                checkFamChar10.setSelected(active);
                break;
            case 11:
                checkFamChar11.setSelected(active);
                break;
            case 12:
                checkFamChar12.setSelected(active);
                break;
            case 13:
                checkFamChar13.setSelected(active);
                break;
            case 14:
                checkFamChar14.setSelected(active);
                break;
            case 15:
                checkFamChar15.setSelected(active);
                break;
            case 16:
                checkFamChar16.setSelected(active); 
                break;    
            default:
                System.out.println("Char: setFamChar - wrong ID " + id);
                break;
        }
    }
    
    private boolean getFamChar(int id){
        switch(id){
            case 1:
                return checkFamChar01.isSelected();
            case 2:
                return checkFamChar02.isSelected();
            case 3:
                return checkFamChar03.isSelected();
            case 4:
                return checkFamChar04.isSelected();
            case 5:
                return checkFamChar05.isSelected();
            case 6:
                return checkFamChar06.isSelected();
            case 7:
                return checkFamChar07.isSelected();
            case 8:
                return checkFamChar08.isSelected();
            case 9:
                return checkFamChar09.isSelected();
            case 10:
                return checkFamChar10.isSelected();
            case 11:
                return checkFamChar11.isSelected();
            case 12:
                return checkFamChar12.isSelected();
            case 13:
                return checkFamChar13.isSelected();
            case 14:
                return checkFamChar14.isSelected();
            case 15:
                return checkFamChar15.isSelected();
            case 16:
                return checkFamChar16.isSelected();
            default:
                System.out.println("Char: getFamChar - wrong ID " + id);
                return false;
        }
    }
    
    public void setSkill(byte[] f){
        int value;
        
        // Skill values come in 2 bytes and are read in reverse
        // First byte to be read is the 2nd one, and we go right to left

        // Skill values in 2nd byte
		
        // The Cyber NT / Newtype bits work in reverse. They disable the skill
        // A character with both bits unselected will be both a Newtype and a Cyber NT
		
        // NOT a Cyber NT ---- ---X
        value = f[1] & 0x01;        
        setSkill(1, value == 1);
        
        // NOT a Newtype ---- --X-
        value = f[1] & 0x02;        
        setSkill(2, value == 2);
        
        // Potential ---- -X--
        // Must have both this checked and defined levels
        value = f[1] & 0x04;        
        setSkill(3, value == 4);
        
        // Shield Defense ---- X---
        value = f[1] & 0x08;        
        setSkill(4, value == 8);
        
        // Support during Attack Phase ---X ----
        // This is checked at the beginning of the phase to give support tokens to the pilot
        value = f[1] & 0x10;        
        setSkill(5, value == 16);
        
        // Support during Defense Phase --X- ----
        // This is checked at the beginning of the phase to give support tokens to the pilot
        value = f[1] & 0x20;        
        setSkill(6, value == 32);
        
        // Command -X-- ----
        // Must have both this checked and defined levels
        value = f[1] & 0x40;        
        setSkill(7, value == 64);
        
        // Instinct X--- ----
        value = f[1] & 0x80;        
        setSkill(8, value == 128);     
		
        // Skill values in 1st byte
		
        // Counter ---- ---X
        value = f[0] & 0x01;        
        setSkill(9, value == 1);
        
        // Hit & Away ---- --X-
        value = f[0] & 0x02;        
        setSkill(10, value == 2);
        
        // Sniping ---- -X--
        value = f[0] & 0x04;        
        setSkill(11, value == 4);
        
        // ??? ---- X---
        value = f[0] & 0x08;        
        setSkill(12, value == 8);
        
        // ??? ---X ----
        value = f[0] & 0x10;        
        setSkill(13, value == 16);
        
        // ??? --X- ----
        value = f[0] & 0x20;        
        setSkill(14, value == 32);
        
        // ??? -X-- ----
        value = f[0] & 0x40;        
        setSkill(15, value == 64);
        
        // ??? X--- ----
        value = f[0] & 0x80;        
        setSkill(16, value == 128);    
    }
    
    public byte[] getSkill(){
        byte[] b = new byte[2];
        
	// Skill values in 2nd byte
		
        // NOT a Cyber NT ---- ---X
        if( getSkill(1) )
            b[1] |= 0x01;
        
        // NOT a Newtype ---- --X-
        if( getSkill(2) )
            b[1] |= 0x02;
        
        // ??? ---- -X--
        if( getSkill(3) )
            b[1] |= 0x04;
        
        // Shield Defense ---- X---
        if( getSkill(4) )
            b[1] |= 0x08;
        
        // ??? ---X ----
        if( getSkill(5) )
            b[1] |= 0x10;
        
        // ??? --X- ----
        if( getSkill(6) )
            b[1] |= 0x20;
        
        // ??? -X-- ----
        if( getSkill(7) )
            b[1] |= 0x40;
        
        // Instinct X--- ----
        if( getSkill(8) )
            b[1] |= 0x80; 
		
	// Skill values in 1st byte
		
        // Counter ---- ---X
        if( getSkill(9) )
            b[0] |= 0x01;
        
        // Hit & Away ---- --X-
        if( getSkill(10) )
            b[0] |= 0x02;
        
        // Sniping ---- -X--
        if( getSkill(11) )
            b[0] |= 0x04;
        
        // ??? ---- X---
        if( getSkill(12) )
            b[0] |= 0x08;
        
        // ??? ---X ----
        if( getSkill(13) )
            b[0] |= 0x10;
        
        // ??? --X- ----
        if( getSkill(14) )
            b[0] |= 0x20;
        
        // ??? -X-- ----
        if( getSkill(15) )
            b[0] |= 0x40;
        
        // ??? X--- ----
        if( getSkill(16) )
            b[0] |= 0x80;  
        
        return b;
    }
    
    private void setSkill(int id, boolean active){
        switch(id){
            case 1:
                checkSkill01.setSelected(active);
                break;
            case 2:
                checkSkill02.setSelected(active);
                break;
            case 3:
                checkSkill03.setSelected(active);
                break;
            case 4:
                checkSkill04.setSelected(active);
                break;
            case 5:
                checkSkill05.setSelected(active);
                break;
            case 6:
                checkSkill06.setSelected(active);
                break;
            case 7:
                checkSkill07.setSelected(active);
                break;
            case 8:
                checkSkill08.setSelected(active); 
                break;    
            case 9:
                checkSkill09.setSelected(active);
                break;
            case 10:
                checkSkill10.setSelected(active);
                break;
            case 11:
                checkSkill11.setSelected(active);
                break;
            case 12:
                checkSkill12.setSelected(active);
                break;
            case 13:
                checkSkill13.setSelected(active);
                break;
            case 14:
                checkSkill14.setSelected(active);
                break;
            case 15:
                checkSkill15.setSelected(active);
                break;
            case 16:
                checkSkill16.setSelected(active); 
                break;    
            default:
                System.out.println("Char: setSkill - wrong ID " + id);
                break;
        }
    }
    
    private boolean getSkill(int id){
        switch(id){
            case 1:
                return checkSkill01.isSelected();
            case 2:
                return checkSkill02.isSelected();
            case 3:
                return checkSkill03.isSelected();
            case 4:
                return checkSkill04.isSelected();
            case 5:
                return checkSkill05.isSelected();
            case 6:
                return checkSkill06.isSelected();
            case 7:
                return checkSkill07.isSelected();
            case 8:
                return checkSkill08.isSelected();
            case 9:
                return checkSkill09.isSelected();
            case 10:
                return checkSkill10.isSelected();
            case 11:
                return checkSkill11.isSelected();
            case 12:
                return checkSkill12.isSelected();
            case 13:
                return checkSkill13.isSelected();
            case 14:
                return checkSkill14.isSelected();
            case 15:
                return checkSkill15.isSelected();
            case 16:
                return checkSkill16.isSelected();
            default:
                System.out.println("Char: getSkill - wrong ID " + id);
                return false;
        }
    }
    
    
    
    
    /*********************************************************/
    /*********** SET methods for calculated fields ***********/
    /*********************************************************/
    
    
    // --------------------------- Units Tab ---------------------------
    
    private void setSellValue(){
        if (fieldReward.getText().isEmpty())
            fieldSell.setText("0");
        else{
            int aux = 2 * Integer.valueOf( fieldReward.getText() );

            fieldSell.setText(String.valueOf( aux ) );
        }
    }
    
    private void setInflIncrease(){
        if (fieldInflation.getText().isEmpty())
            fieldIncrease.setText("0");
        else{
            int aux = 2000 * Integer.valueOf( fieldInflation.getText() );

            fieldIncrease.setText(String.valueOf( aux ) );
        }
    }
    
    private void setBodyMax(){
        if (fieldBodyBase.getText().isEmpty())
            fieldBodyMax.setText("0");
        else{
            int aux = Integer.valueOf( fieldBodyBase.getText() );
            double increase = (aux * 0.05)*comboUpgradesHP.getSelectedIndex();
            BigDecimal bd = new BigDecimal(String.valueOf(increase));

            fieldBodyMax.setText(String.valueOf( aux + bd.intValue() ) );
        }
    }
    
    private void setHeadMax(){
        if (fieldHeadBase.getText().isEmpty())
            fieldHeadMax.setText("0");
        else{
            int aux = Integer.valueOf( fieldHeadBase.getText() );
            double increase = (aux * 0.05)*comboUpgradesHP.getSelectedIndex();
            BigDecimal bd = new BigDecimal(String.valueOf(increase));

            fieldHeadMax.setText(String.valueOf( aux + bd.intValue() ) );
        }
    }
    
    private void setArmsMax(){
        if (fieldArmsBase.getText().isEmpty())
            fieldArmsMax.setText("0");
        else{
            int aux = Integer.valueOf( fieldArmsBase.getText() );
            double increase = (aux * 0.05)*comboUpgradesHP.getSelectedIndex();
            BigDecimal bd = new BigDecimal(String.valueOf(increase));

            fieldArmsMax.setText(String.valueOf( aux + bd.intValue() ) );
        }
    }
    
    private void setLegsMax(){
        if (fieldLegsBase.getText().isEmpty())
            fieldLegsMax.setText("0");
        else{
            int aux = Integer.valueOf( fieldLegsBase.getText() );
            double increase = (aux * 0.05)*comboUpgradesHP.getSelectedIndex();
            BigDecimal bd = new BigDecimal(String.valueOf(increase));

            fieldLegsMax.setText(String.valueOf( aux + bd.intValue() ) );
        }
    }
    
    private void setEnergyMax(){
        if (fieldEnergyBase.getText().isEmpty())
            fieldEnergyMax.setText("0");
        else{
            int aux = Integer.valueOf( fieldEnergyBase.getText() );
            double increase = (aux * 0.1)*comboUpgradesEN.getSelectedIndex();   // Energy grows by 10% instead of 5%, unlike the others
            BigDecimal bd = new BigDecimal(String.valueOf(increase));

            fieldEnergyMax.setText(String.valueOf( aux + bd.intValue() ) );
        }
    }
    
    private void setMobilityMax(){
        if (fieldMobilityBase.getText().isEmpty())
            fieldMobilityMax.setText("0");
        else{
            int aux = Integer.valueOf( fieldMobilityBase.getText() );
            double increase = (aux * 0.05)*comboUpgradesMob.getSelectedIndex();
            BigDecimal bd = new BigDecimal(String.valueOf(increase));

            fieldMobilityMax.setText(String.valueOf( aux + bd.intValue() ) );
        }
    }
    
    private void setArmorMax(){
        if (fieldArmorBase.getText().isEmpty())
            fieldArmorMax.setText("0");
        else{
            int aux = Integer.valueOf( fieldArmorBase.getText() );
            double increase = (aux * 0.05)*comboUpgradesArmor.getSelectedIndex();
            BigDecimal bd = new BigDecimal(String.valueOf(increase));

            fieldArmorMax.setText(String.valueOf( aux + bd.intValue() ) );
        }
    }
    
    private void setUpgradesWeapons(){
        Component[] weapPanels = panelWeapList.getComponents();
        WeaponPanel wp;
        
        for (int i = 0; i < weapPanels.length; i++){
            wp = (WeaponPanel) weapPanels[i];
            wp.setUpgrades(comboUpgradesWeapons.getSelectedIndex());
        }
    }
    
    private void setBody(){
        int aux = comboBody.getSelectedIndex();
        
        switch (aux) {
            case 0: // Unused
                labelBody.setText("--------");
                fieldBodyBase.setText("-1");
                fieldBodyBase.setEnabled(false);
                fieldBodyMax.setText("-1");
                break;
            case 1: // Robot
                labelBody.setText("BODY HP");
                fieldBodyBase.setEnabled(true);
                break;
            case 2: // Ship
                labelBody.setText("BODY HP");
                fieldBodyBase.setEnabled(true);
                break;
            default:
                break;
        }
    }
    
    private void setHead(){
        int aux = comboHead.getSelectedIndex();
        
        switch (aux) {
            case 0: // Unused
                labelHead.setText("--------");
                fieldHeadBase.setText("-1");
                fieldHeadBase.setEnabled(false);
                fieldHeadMax.setText("-1");
                break;
            case 1: // Robot
                labelHead.setText("HEAD HP");
                fieldHeadBase.setEnabled(true);
                break;
            case 2: // Ship
                labelHead.setText("CONTROLS HP");
                fieldHeadBase.setEnabled(true);
                break;
            default:
                break;
        }
    }
    
    private void setArms(){
        int aux = comboArms.getSelectedIndex();
        
        switch (aux) {
            case 0: // Unused
                labelArms.setText("--------");
                fieldArmsBase.setText("-1");
                fieldArmsBase.setEnabled(false);
                fieldArmsMax.setText("-1");
                break;
            case 1: // Robot
                labelArms.setText("ARMS HP");
                fieldArmsBase.setEnabled(true);
                break;
            case 2: // Ship
                labelArms.setText("WEAPONS HP");
                fieldArmsBase.setEnabled(true);
                break;
            default:
                break;
        }
    }
    
    private void setLegs(){
        int aux = comboLegs.getSelectedIndex();
        
        switch (aux) {
            case 0: // Unused
                labelLegs.setText("--------");
                fieldLegsBase.setText("-1");
                fieldLegsBase.setEnabled(false);
                fieldLegsMax.setText("-1");
                break;
            case 1: // Robot
                labelLegs.setText("LEGS HP");
                fieldLegsBase.setEnabled(true);
                break;
            case 2: // Ship
                labelLegs.setText("ENGINES HP");
                fieldLegsBase.setEnabled(true);
                break;
            default:
                break;
        }
    }
    
    
    // --------------------------- Character Tab ---------------------------
    
    
    private void setPersonalityInfo(){
        switch (comboPersonality.getSelectedIndex()){
            case 0:     // Normal
                fieldPersHit.setText("+0");
                fieldPersMiss.setText("+0");
                fieldPersEvade.setText("+0");
                fieldPersDamage.setText("+1");
                fieldPersEnemy.setText("+3");
                fieldPersAlly.setText("+0");
                break;
            case 1:     // Super strong
                fieldPersHit.setText("+2");
                fieldPersMiss.setText("-1");
                fieldPersEvade.setText("+0");
                fieldPersDamage.setText("+2");
                fieldPersEnemy.setText("+3");
                fieldPersAlly.setText("+2");
                break;
            case 2:     // Strong
                fieldPersHit.setText("+1");
                fieldPersMiss.setText("+0");
                fieldPersEvade.setText("+1");
                fieldPersDamage.setText("+1");
                fieldPersEnemy.setText("+3");
                fieldPersAlly.setText("+1");
                break;
            case 3:     // Cool
                fieldPersHit.setText("+2");
                fieldPersMiss.setText("-1");
                fieldPersEvade.setText("+2");
                fieldPersDamage.setText("-1");
                fieldPersEnemy.setText("+2");
                fieldPersAlly.setText("+0");
                break;
            case 4:     // Cautious
                fieldPersHit.setText("+1");
                fieldPersMiss.setText("+0");
                fieldPersEvade.setText("+1");
                fieldPersDamage.setText("+1");
                fieldPersEnemy.setText("+3");
                fieldPersAlly.setText("-1");
                break;
            case 5:     // Small fry (enemy only)
                fieldPersHit.setText("+1");
                fieldPersMiss.setText("+1");
                fieldPersEvade.setText("+1");
                fieldPersDamage.setText("+1");
                fieldPersEnemy.setText("+3");
                fieldPersAlly.setText("+3");
                break;
            case 6:     // Mid-boss (enemy only)
                fieldPersHit.setText("+2");
                fieldPersMiss.setText("+2");
                fieldPersEvade.setText("+2");
                fieldPersDamage.setText("+2");
                fieldPersEnemy.setText("+3");
                fieldPersAlly.setText("+3");
                break;
            case 7:     // Boss (enemy only)
                fieldPersHit.setText("+3");
                fieldPersMiss.setText("+3");
                fieldPersEvade.setText("+3");
                fieldPersDamage.setText("+3");
                fieldPersEnemy.setText("+3");
                fieldPersAlly.setText("+3");
                break;
            case 8:     // Buildings
                fieldPersHit.setText("+0?");
                fieldPersMiss.setText("+0?");
                fieldPersEvade.setText("+0?");
                fieldPersDamage.setText("+0?");
                fieldPersEnemy.setText("+0?");
                fieldPersAlly.setText("+0?");
                break;
            case 9:     // Subpilots
                fieldPersHit.setText("+0");
                fieldPersMiss.setText("+0");
                fieldPersEvade.setText("+0");
                fieldPersDamage.setText("+0");
                fieldPersEnemy.setText("+0");
                fieldPersAlly.setText("+0");
                break;
            case 10:    // NPCs
                fieldPersHit.setText("+0");
                fieldPersMiss.setText("+0");
                fieldPersEvade.setText("+0");
                fieldPersDamage.setText("+0");
                fieldPersEnemy.setText("+0");
                fieldPersAlly.setText("+0");
                break;
        }
    }
    
    private void setMaxStats(){
        setMeleeMax();
        setRangedMax();
        setDefenseMax();
        setSkillMax();
        setAccuracyMax();
        setEvasionMax();
        setSPMax();
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                labelMeleeRank.setText("B");
                labelRangedRank.setText("B");
                labelDefenseRank.setText("B");
                labelSkillRank.setText("B");
                labelAccuracyRank.setText("S");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            case 1:     // Melee
                labelMeleeRank.setText("S");
                labelRangedRank.setText("B");
                labelDefenseRank.setText("B");
                labelSkillRank.setText("B");
                labelAccuracyRank.setText("A");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            case 2:     // Melee + Defense
                labelMeleeRank.setText("S");
                labelRangedRank.setText("B");
                labelDefenseRank.setText("A");
                labelSkillRank.setText("B");
                labelAccuracyRank.setText("A");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            case 3:     // Melee + Skill
                labelMeleeRank.setText("S");
                labelRangedRank.setText("B");
                labelDefenseRank.setText("B");
                labelSkillRank.setText("A");
                labelAccuracyRank.setText("A");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            case 4:     // Ranged
                labelMeleeRank.setText("B");
                labelRangedRank.setText("S");
                labelDefenseRank.setText("B");
                labelSkillRank.setText("B");
                labelAccuracyRank.setText("A");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            case 5:     // Ranged + Defense
                labelMeleeRank.setText("B");
                labelRangedRank.setText("S");
                labelDefenseRank.setText("A");
                labelSkillRank.setText("B");
                labelAccuracyRank.setText("A");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            case 6:     // Ranged + Skill
                labelMeleeRank.setText("B");
                labelRangedRank.setText("S");
                labelDefenseRank.setText("B");
                labelSkillRank.setText("A");
                labelAccuracyRank.setText("A");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            case 7:     // Well rounded
                labelMeleeRank.setText("A");
                labelRangedRank.setText("A");
                labelDefenseRank.setText("A");
                labelSkillRank.setText("A");
                labelAccuracyRank.setText("A");
                labelEvasionRank.setText("B");
                labelSPRank.setText("B");
                break;
            default:
                labelMeleeRank.setText("-");
                labelRangedRank.setText("-");
                labelDefenseRank.setText("-");
                labelSkillRank.setText("-");
                labelAccuracyRank.setText("-");
                labelEvasionRank.setText("-");
                labelSPRank.setText("-");
                break;
        }
    }
    
    private void setMeleeMax(){
        int aux = 0;
        
        if (!fieldMeleeBase.getText().isEmpty())
            aux = Integer.valueOf(fieldMeleeBase.getText());
        
        if (comboPersonality.getSelectedIndex() > 7){
            fieldMeleeMax.setText( "-" );
            return;
        }
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                fieldMeleeMax.setText( "" + ( aux + 98 ) );
                break;
            case 1:     // Melee
                fieldMeleeMax.setText( "" + ( aux + 147 ) );
                break;
            case 2:     // Melee + Defense
                fieldMeleeMax.setText( "" + ( aux + 147 ) );
                break;
            case 3:     // Melee + Skill
                fieldMeleeMax.setText( "" + ( aux + 147 ) );
                break;
            case 4:     // Ranged
                fieldMeleeMax.setText( "" + ( aux + 98 ) );
                break;
            case 5:     // Ranged + Defense
                fieldMeleeMax.setText( "" + ( aux + 98 ) );
                break;
            case 6:     // Ranged + Skill
                fieldMeleeMax.setText( "" + ( aux + 98 ) );
                break;
            case 7:     // Well rounded
                fieldMeleeMax.setText( "" + ( aux + 131 ) );
                break;
            default:
                fieldMeleeMax.setText( "-" );
                break;                
        }
    }
    
    private void setRangedMax(){
        int aux = 0;
        
        if (!fieldRangedBase.getText().isEmpty())
            aux = Integer.valueOf(fieldRangedBase.getText());
        
        if (comboPersonality.getSelectedIndex() > 7){
            fieldRangedMax.setText( "-" );
            return;
        }
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                fieldRangedMax.setText( "" + ( aux + 98 ) );
                break;
            case 1:     // Melee
                fieldRangedMax.setText( "" + ( aux + 98 ) );
                break;
            case 2:     // Melee + Defense
                fieldRangedMax.setText( "" + ( aux + 98 ) );
                break;
            case 3:     // Melee + Skill
                fieldRangedMax.setText( "" + ( aux + 98 ) );
                break;
            case 4:     // Ranged
                fieldRangedMax.setText( "" + ( aux + 147 ) );
                break;
            case 5:     // Ranged + Defense
                fieldRangedMax.setText( "" + ( aux + 147 ) );
                break;
            case 6:     // Ranged + Skill
                fieldRangedMax.setText( "" + ( aux + 147 ) );
                break;
            case 7:     // Well rounded
                fieldRangedMax.setText( "" + ( aux + 131 ) );
                break;
            default:
                fieldRangedMax.setText( "-" );
                break;        
        }
    }
    
    private void setDefenseMax(){
        int aux = 0;
        
        if (!fieldDefenseBase.getText().isEmpty())
            aux = Integer.valueOf(fieldDefenseBase.getText());
        
        if (comboPersonality.getSelectedIndex() > 7){
            fieldDefenseMax.setText( "-" );
            return;
        }
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                fieldDefenseMax.setText( "" + ( aux + 98 ) );
                break;
            case 1:     // Melee
                fieldDefenseMax.setText( "" + ( aux + 98 ) );
                break;
            case 2:     // Melee + Defense
                fieldDefenseMax.setText( "" + ( aux + 131 ) );
                break;
            case 3:     // Melee + Skill
                fieldDefenseMax.setText( "" + ( aux + 98 ) );
                break;
            case 4:     // Ranged
                fieldDefenseMax.setText( "" + ( aux + 98 ) );
                break;
            case 5:     // Ranged + Defense
                fieldDefenseMax.setText( "" + ( aux + 131 ) );
                break;
            case 6:     // Ranged + Skill
                fieldDefenseMax.setText( "" + ( aux + 98 ) );
                break;
            case 7:     // Well rounded
                fieldDefenseMax.setText( "" + ( aux + 131 ) );
                break;
            default:
                fieldDefenseMax.setText( "-" );
                break;        
        }
    }
    
    private void setSkillMax(){
        int aux = 0;
        
        if (!fieldSkillBase.getText().isEmpty())
            aux = Integer.valueOf(fieldSkillBase.getText());
        
        if (comboPersonality.getSelectedIndex() > 7){
            fieldSkillMax.setText( "-" );
            return;
        }
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                fieldSkillMax.setText( "" + ( aux + 98 ) );
                break;
            case 1:     // Melee
                fieldSkillMax.setText( "" + ( aux + 98 ) );
                break;
            case 2:     // Melee + Defense
                fieldSkillMax.setText( "" + ( aux + 98 ) );
                break;
            case 3:     // Melee + Skill
                fieldSkillMax.setText( "" + ( aux + 131 ) );
                break;
            case 4:     // Ranged
                fieldSkillMax.setText( "" + ( aux + 98 ) );
                break;
            case 5:     // Ranged + Defense
                fieldSkillMax.setText( "" + ( aux + 98 ) );
                break;
            case 6:     // Ranged + Skill
                fieldSkillMax.setText( "" + ( aux + 131 ) );
                break;
            case 7:     // Well rounded
                fieldSkillMax.setText( "" + ( aux + 131 ) );
                break;
            default:
                fieldSkillMax.setText( "-" );
                break;        
        }
    }
    
    private void setAccuracyMax(){
        int aux = 0;
        
        if (!fieldAccuracyBase.getText().isEmpty())
            aux = Integer.valueOf(fieldAccuracyBase.getText());
        
        if (comboPersonality.getSelectedIndex() > 7){
            fieldAccuracyMax.setText( "-" );
            return;
        }
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                fieldAccuracyMax.setText( "" + ( aux + 147 ) );
                break;
            case 1:     // Melee
                fieldAccuracyMax.setText( "" + ( aux + 131 ) );
                break;
            case 2:     // Melee + Defense
                fieldAccuracyMax.setText( "" + ( aux + 131 ) );
                break;
            case 3:     // Melee + Skill
                fieldAccuracyMax.setText( "" + ( aux + 131 ) );
                break;
            case 4:     // Ranged
                fieldAccuracyMax.setText( "" + ( aux + 131 ) );
                break;
            case 5:     // Ranged + Defense
                fieldAccuracyMax.setText( "" + ( aux + 131 ) );
                break;
            case 6:     // Ranged + Skill
                fieldAccuracyMax.setText( "" + ( aux + 131 ) );
                break;
            case 7:     // Well rounded
                fieldAccuracyMax.setText( "" + ( aux + 131 ) );
                break;
            default:
                fieldAccuracyMax.setText( "-" );
                break;        
        }
    }
    
    private void setEvasionMax(){
        int aux = 0;
        
        if (!fieldEvasionBase.getText().isEmpty())
            aux = Integer.valueOf(fieldEvasionBase.getText());
        
        if (comboPersonality.getSelectedIndex() > 7){
            fieldEvasionMax.setText( "-" );
            return;
        }
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            case 1:     // Melee
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            case 2:     // Melee + Defense
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            case 3:     // Melee + Skill
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            case 4:     // Ranged
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            case 5:     // Ranged + Defense
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            case 6:     // Ranged + Skill
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            case 7:     // Well rounded
                fieldEvasionMax.setText( "" + ( aux + 98 ) );
                break;
            default:
                fieldEvasionMax.setText( "-" );
                break;        
        }
    }
    
    private void setSPMax(){
        int aux = 0;
        
        if (!fieldSPBase.getText().isEmpty())
            aux = Integer.valueOf(fieldSPBase.getText());
        
        if (comboPersonality.getSelectedIndex() == 8 || comboPersonality.getSelectedIndex() == 10){
            fieldSPMax.setText( "-" );
            return;
        }
        
        switch(comboGrowthSchema.getSelectedIndex()){
            case 0:     // Mediocre (Accuracy)
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            case 1:     // Melee
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            case 2:     // Melee + Defense
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            case 3:     // Melee + Skill
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            case 4:     // Ranged
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            case 5:     // Ranged + Defense
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            case 6:     // Ranged + Skill
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            case 7:     // Well rounded
                fieldSPMax.setText( "" + ( aux + 98 ) );
                break;
            default:
                fieldSPMax.setText( "-" );
                break;        
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkAbil01;
    private javax.swing.JCheckBox checkAbil02;
    private javax.swing.JCheckBox checkAbil03;
    private javax.swing.JCheckBox checkAbil04;
    private javax.swing.JCheckBox checkAbil05;
    private javax.swing.JCheckBox checkAbil06;
    private javax.swing.JCheckBox checkAbil07;
    private javax.swing.JCheckBox checkAbil08;
    private javax.swing.JCheckBox checkAbil09;
    private javax.swing.JCheckBox checkAbil10;
    private javax.swing.JCheckBox checkAbil11;
    private javax.swing.JCheckBox checkAbil12;
    private javax.swing.JCheckBox checkAbil13;
    private javax.swing.JCheckBox checkAbil14;
    private javax.swing.JCheckBox checkAbil15;
    private javax.swing.JCheckBox checkAbil16;
    private javax.swing.JCheckBox checkAbil17;
    private javax.swing.JCheckBox checkAbil18;
    private javax.swing.JCheckBox checkAbil19;
    private javax.swing.JCheckBox checkAbil20;
    private javax.swing.JCheckBox checkAbil21;
    private javax.swing.JCheckBox checkAbil22;
    private javax.swing.JCheckBox checkAbil23;
    private javax.swing.JCheckBox checkAbil24;
    private javax.swing.JCheckBox checkAbil25;
    private javax.swing.JCheckBox checkAbil26;
    private javax.swing.JCheckBox checkAir;
    private javax.swing.JCheckBox checkBuilding;
    private javax.swing.JCheckBox checkFamChar01;
    private javax.swing.JCheckBox checkFamChar02;
    private javax.swing.JCheckBox checkFamChar03;
    private javax.swing.JCheckBox checkFamChar04;
    private javax.swing.JCheckBox checkFamChar05;
    private javax.swing.JCheckBox checkFamChar06;
    private javax.swing.JCheckBox checkFamChar07;
    private javax.swing.JCheckBox checkFamChar08;
    private javax.swing.JCheckBox checkFamChar09;
    private javax.swing.JCheckBox checkFamChar10;
    private javax.swing.JCheckBox checkFamChar11;
    private javax.swing.JCheckBox checkFamChar12;
    private javax.swing.JCheckBox checkFamChar13;
    private javax.swing.JCheckBox checkFamChar14;
    private javax.swing.JCheckBox checkFamChar15;
    private javax.swing.JCheckBox checkFamChar16;
    private javax.swing.JCheckBox checkFamUnit01;
    private javax.swing.JCheckBox checkFamUnit02;
    private javax.swing.JCheckBox checkFamUnit03;
    private javax.swing.JCheckBox checkFamUnit04;
    private javax.swing.JCheckBox checkFamUnit05;
    private javax.swing.JCheckBox checkFamUnit06;
    private javax.swing.JCheckBox checkFamUnit07;
    private javax.swing.JCheckBox checkFamUnit08;
    private javax.swing.JCheckBox checkFamUnit09;
    private javax.swing.JCheckBox checkFamUnit10;
    private javax.swing.JCheckBox checkFamUnit11;
    private javax.swing.JCheckBox checkFamUnit12;
    private javax.swing.JCheckBox checkFamUnit13;
    private javax.swing.JCheckBox checkFamUnit14;
    private javax.swing.JCheckBox checkFamUnit15;
    private javax.swing.JCheckBox checkFamUnit16;
    private javax.swing.JCheckBox checkGround;
    private javax.swing.JCheckBox checkHover;
    private javax.swing.JCheckBoxMenuItem checkItemSafety;
    private javax.swing.JCheckBox checkLand;
    private javax.swing.JCheckBox checkShield;
    private javax.swing.JCheckBox checkSkill01;
    private javax.swing.JCheckBox checkSkill02;
    private javax.swing.JCheckBox checkSkill03;
    private javax.swing.JCheckBox checkSkill04;
    private javax.swing.JCheckBox checkSkill05;
    private javax.swing.JCheckBox checkSkill06;
    private javax.swing.JCheckBox checkSkill07;
    private javax.swing.JCheckBox checkSkill08;
    private javax.swing.JCheckBox checkSkill09;
    private javax.swing.JCheckBox checkSkill10;
    private javax.swing.JCheckBox checkSkill11;
    private javax.swing.JCheckBox checkSkill12;
    private javax.swing.JCheckBox checkSkill13;
    private javax.swing.JCheckBox checkSkill14;
    private javax.swing.JCheckBox checkSkill15;
    private javax.swing.JCheckBox checkSkill16;
    private javax.swing.JCheckBox checkWater;
    private javax.swing.JComboBox<String> comboAir;
    private javax.swing.JComboBox<String> comboAlly;
    private javax.swing.JComboBox<String> comboArms;
    private javax.swing.JComboBox<String> comboBGM;
    private javax.swing.JComboBox<String> comboBody;
    private javax.swing.JComboBox<String> comboCapture;
    private javax.swing.JComboBox<String> comboChars;
    private javax.swing.JComboBox<String> comboCommand1;
    private javax.swing.JComboBox<String> comboCommand2;
    private javax.swing.JComboBox<String> comboCommand3;
    private javax.swing.JComboBox<String> comboCommand4;
    private javax.swing.JComboBox<String> comboCommand5;
    private javax.swing.JComboBox<String> comboCommand6;
    private javax.swing.JComboBox<String> comboGrowthSchema;
    private javax.swing.JComboBox<String> comboHead;
    private javax.swing.JComboBox<String> comboItem;
    private javax.swing.JComboBox<String> comboLand;
    private javax.swing.JComboBox<String> comboLegs;
    private javax.swing.JComboBox<String> comboMove;
    private javax.swing.JComboBox<String> comboParts;
    private javax.swing.JComboBox<String> comboPersonality;
    private javax.swing.JComboBox<String> comboSeries;
    private javax.swing.JComboBox<String> comboSeriesChar;
    private javax.swing.JComboBox<String> comboSize;
    private javax.swing.JComboBox<String> comboSkillAces;
    private javax.swing.JComboBox<String> comboSkillParts;
    private javax.swing.JComboBox<String> comboSpace;
    private javax.swing.JComboBox<String> comboUnits;
    private javax.swing.JComboBox<String> comboUpgradesArmor;
    private javax.swing.JComboBox<String> comboUpgradesEN;
    private javax.swing.JComboBox<String> comboUpgradesHP;
    private javax.swing.JComboBox<String> comboUpgradesMob;
    private javax.swing.JComboBox<String> comboUpgradesWeapons;
    private javax.swing.JComboBox<String> comboWater;
    private javax.swing.JTextField fieldAI;
    private javax.swing.JTextField fieldAccuracyBase;
    private javax.swing.JTextField fieldAccuracyMax;
    private javax.swing.JTextField fieldArmorBase;
    private javax.swing.JTextField fieldArmorMax;
    private javax.swing.JTextField fieldArmsBase;
    private javax.swing.JTextField fieldArmsMax;
    private javax.swing.JTextField fieldBodyBase;
    private javax.swing.JTextField fieldBodyMax;
    private javax.swing.JTextField fieldByte37;
    private javax.swing.JTextField fieldByte38;
    private javax.swing.JTextField fieldByte39;
    private javax.swing.JTextField fieldByte56;
    private javax.swing.JTextField fieldByte60;
    private javax.swing.JTextField fieldByte61;
    private javax.swing.JTextField fieldByte62;
    private javax.swing.JTextField fieldByte69;
    private javax.swing.JTextField fieldByte71;
    private javax.swing.JTextField fieldByte72;
    private javax.swing.JTextField fieldByteChar70;
    private javax.swing.JTextField fieldByteChar71;
    private javax.swing.JTextField fieldByteChar72;
    private javax.swing.JTextField fieldByteChar79;
    private javax.swing.JTextField fieldByteChar80;
    private javax.swing.JTextField fieldByteChar81;
    private javax.swing.JTextField fieldByteChar82;
    private javax.swing.JTextField fieldByteChar83;
    private javax.swing.JTextField fieldByteChar84;
    private javax.swing.JTextField fieldCommandLv1;
    private javax.swing.JTextField fieldCommandLv2;
    private javax.swing.JTextField fieldCommandLv3;
    private javax.swing.JTextField fieldCommandLv4;
    private javax.swing.JTextField fieldCostCommand1;
    private javax.swing.JTextField fieldCostCommand2;
    private javax.swing.JTextField fieldCostCommand3;
    private javax.swing.JTextField fieldCostCommand4;
    private javax.swing.JTextField fieldCostCommand5;
    private javax.swing.JTextField fieldCostCommand6;
    private javax.swing.JTextField fieldDefenseBase;
    private javax.swing.JTextField fieldDefenseMax;
    private javax.swing.JTextField fieldEnemyAI;
    private javax.swing.JTextField fieldEnergyBase;
    private javax.swing.JTextField fieldEnergyMax;
    private javax.swing.JTextField fieldEssential;
    private javax.swing.JTextField fieldEvasionBase;
    private javax.swing.JTextField fieldEvasionMax;
    private javax.swing.JTextField fieldHeadBase;
    private javax.swing.JTextField fieldHeadMax;
    private javax.swing.JTextField fieldIncrease;
    private javax.swing.JTextField fieldInflation;
    private javax.swing.JTextField fieldLearnCommand1;
    private javax.swing.JTextField fieldLearnCommand2;
    private javax.swing.JTextField fieldLearnCommand3;
    private javax.swing.JTextField fieldLearnCommand4;
    private javax.swing.JTextField fieldLearnCommand5;
    private javax.swing.JTextField fieldLearnCommand6;
    private javax.swing.JTextField fieldLegsBase;
    private javax.swing.JTextField fieldLegsMax;
    private javax.swing.JTextField fieldLibID;
    private javax.swing.JTextField fieldLibIDChar;
    private javax.swing.JTextField fieldMeleeBase;
    private javax.swing.JTextField fieldMeleeMax;
    private javax.swing.JTextField fieldMobilityBase;
    private javax.swing.JTextField fieldMobilityMax;
    private javax.swing.JTextField fieldModelID;
    private javax.swing.JTextField fieldNTlv1;
    private javax.swing.JTextField fieldNTlv2;
    private javax.swing.JTextField fieldNTlv3;
    private javax.swing.JTextField fieldNTlv4;
    private javax.swing.JTextField fieldNTlv5;
    private javax.swing.JTextField fieldNTlv6;
    private javax.swing.JTextField fieldNTlv7;
    private javax.swing.JTextField fieldNTlv8;
    private javax.swing.JTextField fieldNTlv9;
    private javax.swing.JTextField fieldPersAlly;
    private javax.swing.JTextField fieldPersDamage;
    private javax.swing.JTextField fieldPersEnemy;
    private javax.swing.JTextField fieldPersEvade;
    private javax.swing.JTextField fieldPersHit;
    private javax.swing.JTextField fieldPersMiss;
    private javax.swing.JTextField fieldPortrait;
    private javax.swing.JTextField fieldPotentialLv1;
    private javax.swing.JTextField fieldPotentialLv2;
    private javax.swing.JTextField fieldPotentialLv3;
    private javax.swing.JTextField fieldPotentialLv4;
    private javax.swing.JTextField fieldPotentialLv5;
    private javax.swing.JTextField fieldPotentialLv6;
    private javax.swing.JTextField fieldPotentialLv7;
    private javax.swing.JTextField fieldPotentialLv8;
    private javax.swing.JTextField fieldPotentialLv9;
    private javax.swing.JTextField fieldRangedBase;
    private javax.swing.JTextField fieldRangedMax;
    private javax.swing.JTextField fieldRepair;
    private javax.swing.JTextField fieldReward;
    private javax.swing.JTextField fieldSPBase;
    private javax.swing.JTextField fieldSPMax;
    private javax.swing.JTextField fieldSell;
    private javax.swing.JTextField fieldSkillBase;
    private javax.swing.JTextField fieldSkillMax;
    private javax.swing.JTextField fieldSupportLv1;
    private javax.swing.JTextField fieldSupportLv2;
    private javax.swing.JTextField fieldSupportLv3;
    private javax.swing.JTextField fieldSupportLv4;
    private javax.swing.JMenuItem itemCharactersExport;
    private javax.swing.JMenuItem itemCharactersImport;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemOpenBin;
    private javax.swing.JMenuItem itemSaveBin;
    private javax.swing.JMenuItem itemUnitsExport;
    private javax.swing.JMenuItem itemUnitsImport;
    private javax.swing.JMenuItem itemWeaponsExport;
    private javax.swing.JMenuItem itemWeaponsImport;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JLabel labelAI;
    private javax.swing.JLabel labelAccuracy;
    private javax.swing.JLabel labelAccuracyRank;
    private javax.swing.JLabel labelAir;
    private javax.swing.JLabel labelAlly;
    private javax.swing.JLabel labelAllyKill;
    private javax.swing.JLabel labelArmor;
    private javax.swing.JLabel labelArms;
    private javax.swing.JLabel labelBGM;
    private javax.swing.JLabel labelBase;
    private javax.swing.JLabel labelBody;
    private javax.swing.JLabel labelByte37;
    private javax.swing.JLabel labelByte38;
    private javax.swing.JLabel labelByte39;
    private javax.swing.JLabel labelByte56;
    private javax.swing.JLabel labelByte60;
    private javax.swing.JLabel labelByte61;
    private javax.swing.JLabel labelByte62;
    private javax.swing.JLabel labelByte69;
    private javax.swing.JLabel labelByte71;
    private javax.swing.JLabel labelByte72;
    private javax.swing.JLabel labelByteChar70;
    private javax.swing.JLabel labelByteChar71;
    private javax.swing.JLabel labelByteChar72;
    private javax.swing.JLabel labelByteChar79;
    private javax.swing.JLabel labelByteChar80;
    private javax.swing.JLabel labelByteChar81;
    private javax.swing.JLabel labelByteChar82;
    private javax.swing.JLabel labelByteChar83;
    private javax.swing.JLabel labelByteChar84;
    private javax.swing.JLabel labelCapture;
    private javax.swing.JLabel labelCharacter;
    private javax.swing.JLabel labelCommand1;
    private javax.swing.JLabel labelCommand2;
    private javax.swing.JLabel labelCommand3;
    private javax.swing.JLabel labelCommand4;
    private javax.swing.JLabel labelCommand5;
    private javax.swing.JLabel labelCommand6;
    private javax.swing.JLabel labelCommandLevel;
    private javax.swing.JLabel labelDamage;
    private javax.swing.JLabel labelDefense;
    private javax.swing.JLabel labelDefenseRank;
    private javax.swing.JLabel labelEnemyAI;
    private javax.swing.JLabel labelEnemyKill;
    private javax.swing.JLabel labelEnergy;
    private javax.swing.JLabel labelEssential;
    private javax.swing.JLabel labelEvade;
    private javax.swing.JLabel labelEvasion;
    private javax.swing.JLabel labelEvasionRank;
    private javax.swing.JLabel labelGrowthSchema;
    private javax.swing.JLabel labelHead;
    private javax.swing.JLabel labelHeader1;
    private javax.swing.JLabel labelHeader2;
    private javax.swing.JLabel labelHeader3;
    private javax.swing.JLabel labelHeader4;
    private javax.swing.JLabel labelHit;
    private javax.swing.JLabel labelIncrease;
    private javax.swing.JLabel labelInflation;
    private javax.swing.JLabel labelItem;
    private javax.swing.JLabel labelLand;
    private javax.swing.JLabel labelLegs;
    private javax.swing.JLabel labelLibID;
    private javax.swing.JLabel labelLibIDChar;
    private javax.swing.JLabel labelMax;
    private javax.swing.JLabel labelMelee;
    private javax.swing.JLabel labelMeleeRank;
    private javax.swing.JLabel labelMiss;
    private javax.swing.JLabel labelMobility;
    private javax.swing.JLabel labelModelID;
    private javax.swing.JLabel labelMovement;
    private javax.swing.JLabel labelNTlevel;
    private javax.swing.JLabel labelParts;
    private javax.swing.JLabel labelPersonality;
    private javax.swing.JLabel labelPortrait;
    private javax.swing.JLabel labelPotentialLevel;
    private javax.swing.JLabel labelRanged;
    private javax.swing.JLabel labelRangedRank;
    private javax.swing.JLabel labelRatings;
    private javax.swing.JLabel labelRepair;
    private javax.swing.JLabel labelReward;
    private javax.swing.JLabel labelSP;
    private javax.swing.JLabel labelSPRank;
    private javax.swing.JLabel labelSell;
    private javax.swing.JLabel labelSeries;
    private javax.swing.JLabel labelSeriesChar;
    private javax.swing.JLabel labelSize;
    private javax.swing.JLabel labelSkill;
    private javax.swing.JLabel labelSkillAces;
    private javax.swing.JLabel labelSkillParts;
    private javax.swing.JLabel labelSkillRank;
    private javax.swing.JLabel labelSpace;
    private javax.swing.JLabel labelSupportLevel;
    private javax.swing.JLabel labelType;
    private javax.swing.JLabel labelUnit;
    private javax.swing.JLabel labelUpgrades;
    private javax.swing.JLabel labelUpgradesWeap;
    private javax.swing.JLabel labelWater;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuImport;
    private javax.swing.JPanel panelAbilities;
    private javax.swing.JPanel panelFamilyChar;
    private javax.swing.JPanel panelFamilyUnit;
    private javax.swing.JPanel panelMisc;
    private javax.swing.JPanel panelPersonality;
    private javax.swing.JPanel panelSkills;
    private javax.swing.JPanel panelSkillsLevel;
    private javax.swing.JPanel panelSpirit;
    private javax.swing.JPanel panelStats;
    private javax.swing.JPanel panelStatsChar;
    private javax.swing.JPanel panelTerrain;
    private javax.swing.JPanel panelUnknown;
    private javax.swing.JPanel panelUnknownChar;
    private javax.swing.JPanel panelWeapList;
    private javax.swing.JPanel panelWeapons;
    private javax.swing.JScrollPane scrollWeapons;
    private javax.swing.JPanel tabCharacters;
    private javax.swing.JPanel tabUnits;
    private javax.swing.JTabbedPane tabsPanel;
    // End of variables declaration//GEN-END:variables
}
