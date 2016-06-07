/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import dias.MemoryStaticVariables.m20150711_load_global_variables;

/**
 *
 * @author Mert
 */
public class VirtualCgm {

    //GS_1 / GS_2-- Debug values: GS1 215 Glucose values samples GS2 21
    double[][] gs1 = new double[1][3 * 24 * 12];
    double[][] gs2 = new double[1][21];

    //gs -- stores readings from GUI
    //1. Obtain current reading
    private double intGS = 188;
    public Matrix gstemp;
    //2. gs stores the las 21 samples
    public Matrix gs;

    public Matrix gsconstant = new Matrix(1, 21);
    public static int kj;

    public VirtualCgm(int kj_in) {
        kj = kj_in;
        
        //Init gstemp matrix -- 21 values equals to init GS
        for (int i = 0; i < 21; i++) {
            gs2[0][i] = intGS;
        }
        gstemp = new Matrix(gs2);
    }

    public Matrix getVirtualCgmValue() {

        setGS1Values();

        //GS temporal  - copied from gs1
        Matrix result = DIAS.createnewMatrix(1, kj + 1, gstemp);
        //21 First samples - input value
        for (int i = 0; i < kj + 1 ; i++) {
            result.set(0, i, gs1[0][i]);
        }

        return result;
    }
    
    public Matrix getVirtualCgmValueFromInput() {

        setGS1Values();
        Matrix gs1Matrix = new Matrix(gs1);

        //Constant matrix with input value 21 times stored
        for (int i = 0; i < 21; i++) {
            gs2[0][i] = m20150711_load_global_variables.gs_in;
        }
        gsconstant = new Matrix(gs2);
        
        //GS temporal 
        gstemp = DIAS.createnewMatrix(1, kj + 1, gstemp);
        //21 First samples - input value
        for (int i = 0; i < 21; i++) {
            gstemp.set(0, i, m20150711_load_global_variables.gs_in);
        }
       //After 21 first samples - old gs values
        for (int i = 21; i < kj + 1; i++) {
            gstemp.set(0, i, gs1Matrix.get(0, i));
        }

        return gstemp;
    }

    private void setGS1Values(){
        //Before 21
        gs1[0][0] = 202;
        gs1[0][1] = 180;
        gs1[0][2] = 176;
        gs1[0][3] = 170;
        gs1[0][4] = 166;
        gs1[0][5] = 158;
        gs1[0][6] = 162;
        gs1[0][7] = 166;
        gs1[0][8] = 168;
        gs1[0][9] = 175;
        gs1[0][10] = 179;
        gs1[0][11] = 180;
        gs1[0][12] = 176;
        gs1[0][13] = 170;
        gs1[0][14] = 166;
        gs1[0][15] = 158;
        gs1[0][16] = 162;
        gs1[0][17] = 166;
        gs1[0][18] = 300;
        gs1[0][19] = 345;
        gs1[0][20] = 398;
        gs1[0][21] = 183;
        //After 21
        gs1[0][22] = 190;
        gs1[0][23] = 206;
        gs1[0][24] = 220;
        gs1[0][25] = 226;
        gs1[0][26] = 238;
        gs1[0][27] = 262;
        gs1[0][28] = 286;
        gs1[0][29] = 168;
        gs1[0][30] = 175;
        gs1[0][31] = 179;
        gs1[0][32] = 186;
        gs1[0][33] = 190;
        gs1[0][34] = 196;
        gs1[0][35] = 200;
        gs1[0][36] = 152;
        gs1[0][37] = 156;
        gs1[0][38] = 160;
        gs1[0][39] = 154;
        gs1[0][40] = 148;
        gs1[0][41] = 142;
        gs1[0][42] = 140;
        gs1[0][43] = 145;
        gs1[0][44] = 152;
        gs1[0][45] = 148;
        gs1[0][46] = 142;
        gs1[0][47] = 136;
        gs1[0][48] = 133;
        gs1[0][49] = 130;
        gs1[0][50] = 128;
        gs1[0][51] = 125;
        gs1[0][52] = 122;
        gs1[0][53] = 118;
        gs1[0][54] = 114;
        gs1[0][55] = 110;
        gs1[0][56] = 107;
        gs1[0][57] = 102;
        gs1[0][58] = 98;
        gs1[0][59] = 90;
        gs1[0][60] = 82;
        gs1[0][61] = 89;
        gs1[0][62] = 95;
        gs1[0][63] = 108;
        gs1[0][64] = 115;
        gs1[0][65] = 132;
        gs1[0][66] = 146;
        gs1[0][67] = 156;
        gs1[0][68] = 166;
        gs1[0][69] = 166;
        gs1[0][70] = 166;
        gs1[0][71] = 166;
        gs1[0][72] = 166;
        gs1[0][73] = 175;
        gs1[0][74] = 179;
        gs1[0][75] = 186;
        gs1[0][76] = 193;
        gs1[0][77] = 208;
        gs1[0][78] = 208;
        gs1[0][79] = 215;
        gs1[0][80] = 226;
        gs1[0][81] = 233;
        gs1[0][82] = 244;
        gs1[0][83] = 240;
        gs1[0][84] = 232;
        gs1[0][85] = 218;
        gs1[0][86] = 210;
        gs1[0][87] = 205;
        gs1[0][88] = 200;
        gs1[0][89] = 196;
        gs1[0][90] = 184;
        gs1[0][91] = 180;
        gs1[0][92] = 176;
        gs1[0][93] = 159;
        gs1[0][94] = 150;
        gs1[0][95] = 145;
        gs1[0][96] = 140;
        gs1[0][97] = 137;
        gs1[0][98] = 133;
        gs1[0][99] = 130;
        gs1[0][100] = 125;
        gs1[0][101] = 125;
        gs1[0][102] = 125;
        gs1[0][103] = 125;
        gs1[0][104] = 125;
        gs1[0][105] = 125;
        gs1[0][106] = 125;
        gs1[0][107] = 125;
        gs1[0][108] = 125;
        gs1[0][109] = 125;
        gs1[0][110] = 125;
        gs1[0][111] = 125;
        gs1[0][112] = 125;
        gs1[0][113] = 125;
        gs1[0][114] = 125;
        gs1[0][115] = 125;
        gs1[0][116] = 125;
        gs1[0][117] = 125;
        gs1[0][117] = 130;
        gs1[0][118] = 135;
        gs1[0][119] = 142;
        gs1[0][120] = 146;
        gs1[0][121] = 154;
        gs1[0][122] = 159;
        gs1[0][123] = 168;
        gs1[0][124] = 172;
        gs1[0][125] = 174;
        gs1[0][126] = 179;
        gs1[0][127] = 185;
        gs1[0][128] = 196;
        gs1[0][129] = 200;
        gs1[0][130] = 205;
        gs1[0][131] = 210;
        gs1[0][132] = 215;
        gs1[0][133] = 220;
        gs1[0][134] = 240;
        gs1[0][135] = 260;
        gs1[0][136] = 280;
        gs1[0][137] = 300;
        gs1[0][138] = 310;
        gs1[0][139] = 330;
        gs1[0][140] = 345;
        gs1[0][141] = 360;
        gs1[0][142] = 385;
        gs1[0][143] = 400;
        gs1[0][144] = 375;
        gs1[0][145] = 360;
        gs1[0][146] = 340;
        gs1[0][147] = 320;
        gs1[0][148] = 300;
        gs1[0][149] = 280;
        gs1[0][150] = 260;
        gs1[0][151] = 240;
        gs1[0][152] = 230;
        gs1[0][153] = 220;
        gs1[0][154] = 200;
        gs1[0][155] = 180;
        gs1[0][156] = 140;
        gs1[0][157] = 130;
        gs1[0][158] = 120;
        gs1[0][159] = 110;
        gs1[0][160] = 100;
        gs1[0][161] = 90;
        gs1[0][162] = 70;
        gs1[0][163] = 90;
        gs1[0][164] = 125;
        gs1[0][165] = 155;
        gs1[0][166] = 170;
        gs1[0][167] = 190;
        gs1[0][168] = 200;
        gs1[0][169] = 230;
        gs1[0][170] = 260;
        gs1[0][171] = 290;
        gs1[0][172] = 320;
        gs1[0][173] = 350;
        gs1[0][174] = 400;
        gs1[0][175] = 420;
        gs1[0][176] = 450;
        gs1[0][177] = 470;
        gs1[0][178] = 500;
        gs1[0][179] = 480;
        gs1[0][180] = 460;
        gs1[0][181] = 430;
        gs1[0][182] = 400;
        gs1[0][183] = 380;
        gs1[0][184] = 340;
        gs1[0][185] = 380;
        gs1[0][186] = 400;
        gs1[0][187] = 420;
        gs1[0][188] = 440;
        gs1[0][189] = 450;
        gs1[0][190] = 470;
        gs1[0][191] = 490;
        gs1[0][192] = 500;
        gs1[0][193] = 530;
        gs1[0][194] = 560;
        gs1[0][195] = 600;
        gs1[0][196] = 570;
        gs1[0][197] = 540;
        gs1[0][198] = 500;
        gs1[0][199] = 400;
        gs1[0][200] = 300;
        gs1[0][201] = 200;
        gs1[0][202] = 100;
        gs1[0][203] = 50;
        gs1[0][204] = 70;
        gs1[0][205] = 80;
        gs1[0][206] = 90;
        gs1[0][207] = 100;
        gs1[0][208] = 110;
        gs1[0][209] = 120;
        gs1[0][210] = 130;
        gs1[0][211] = 140;
        gs1[0][212] = 150;
        gs1[0][213] = 160;
        gs1[0][214] = 170;
        gs1[0][215] = 180;
    }
}
