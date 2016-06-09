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

    
    //gs -- stores readings from GUI
    //1.Initial values
    private final double initialGS = 188;
    private final int INIT_SIZE = 20;
    //GS Matrices: 
    //1. Virtually generated GS 
    double[][] gsVirtual = new double[1][3 * 24 * 12];
    //2. 
    double[][] gs2 = new double[1][INIT_SIZE+1];
    //3.
    public Matrix gstemp;
    //2. gs stores the las 21 samples
    public Matrix gs;

    public Matrix gsconstant = new Matrix(1, INIT_SIZE+1);
    public static int kj;

    public VirtualCgm(int kj_in) {
        kj = kj_in;
        
        //Init gstemp matrix -- 21 values equals to init GS
        for (int i = 0; i <= INIT_SIZE; i++) {
            gs2[0][i] = initialGS;
        }
        gstemp = new Matrix(gs2);
        
        //Initialize the virtually generated values
        setGSVirtualValues();

    }

    public Matrix getVirtualCgmValue() {


        //GS temporal  - copied from gs1
        Matrix result = DIAS.createnewMatrix(1, kj + 1, gstemp);
        //21 First samples - input value
        for (int i = 0; i < kj + 1 ; i++) {
            result.set(0, i, gsVirtual[0][i]);
        }

        return result;
    }
    
    public Matrix getVirtualCgmValueFromInput() {

        setGSVirtualValues();
        Matrix gs1Matrix = new Matrix(gsVirtual);

        //Constant matrix with input value 21 times stored
        for (int i = 0; i <= INIT_SIZE; i++) {
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

    private void setGSVirtualValues(){
        //Before 21
        gsVirtual[0][0] = 183;
        gsVirtual[0][1] = 180;
        gsVirtual[0][2] = 176;
        gsVirtual[0][3] = 170;
        gsVirtual[0][4] = 166;
        gsVirtual[0][5] = 158;
        gsVirtual[0][6] = 162;
        gsVirtual[0][7] = 166;
        gsVirtual[0][8] = 168;
        gsVirtual[0][9] = 175;
        gsVirtual[0][10] = 179;
        
        gsVirtual[0][11] = 180;
        gsVirtual[0][12] = 176;
        gsVirtual[0][13] = 170;
        gsVirtual[0][14] = 166;
        gsVirtual[0][15] = 158;
        gsVirtual[0][16] = 162;
        gsVirtual[0][17] = 166;
        gsVirtual[0][18] = 300;
        gsVirtual[0][19] = 345;
        gsVirtual[0][20] = 398;
        //After 21
        //After 21
        gsVirtual[0][21] = 183;
        gsVirtual[0][22] = 190;
        gsVirtual[0][23] = 206;
        gsVirtual[0][24] = 220;
        gsVirtual[0][25] = 226;
        gsVirtual[0][26] = 238;
        gsVirtual[0][27] = 262;
        gsVirtual[0][28] = 286;
        gsVirtual[0][29] = 168;
        gsVirtual[0][30] = 175;
        gsVirtual[0][31] = 179;
        gsVirtual[0][32] = 186;
        gsVirtual[0][33] = 190;
        gsVirtual[0][34] = 196;
        gsVirtual[0][35] = 200;
        gsVirtual[0][36] = 152;
        gsVirtual[0][37] = 156;
        gsVirtual[0][38] = 160;
        gsVirtual[0][39] = 154;
        gsVirtual[0][40] = 148;
        gsVirtual[0][41] = 142;
        gsVirtual[0][42] = 140;
        gsVirtual[0][43] = 145;
        gsVirtual[0][44] = 152;
        gsVirtual[0][45] = 148;
        gsVirtual[0][46] = 142;
        gsVirtual[0][47] = 136;
        gsVirtual[0][48] = 133;
        gsVirtual[0][49] = 130;
        gsVirtual[0][50] = 128;
        gsVirtual[0][51] = 125;
        gsVirtual[0][52] = 122;
        gsVirtual[0][53] = 118;
        gsVirtual[0][54] = 114;
        gsVirtual[0][55] = 110;
        gsVirtual[0][56] = 107;
        gsVirtual[0][57] = 102;
        gsVirtual[0][58] = 98;
        gsVirtual[0][59] = 90;
        gsVirtual[0][60] = 82;
        gsVirtual[0][61] = 89;
        gsVirtual[0][62] = 95;
        gsVirtual[0][63] = 108;
        gsVirtual[0][64] = 115;
        gsVirtual[0][65] = 132;
        gsVirtual[0][66] = 146;
        gsVirtual[0][67] = 156;
        gsVirtual[0][68] = 166;
        gsVirtual[0][69] = 166;
        gsVirtual[0][70] = 166;
        gsVirtual[0][71] = 166;
        gsVirtual[0][72] = 166;
        gsVirtual[0][73] = 175;
        gsVirtual[0][74] = 179;
        gsVirtual[0][75] = 186;
        gsVirtual[0][76] = 193;
        gsVirtual[0][77] = 208;
        gsVirtual[0][78] = 208;
        gsVirtual[0][79] = 215;
        gsVirtual[0][80] = 226;
        gsVirtual[0][81] = 233;
        gsVirtual[0][82] = 244;
        gsVirtual[0][83] = 240;
        gsVirtual[0][84] = 232;
        gsVirtual[0][85] = 218;
        gsVirtual[0][86] = 210;
        gsVirtual[0][87] = 205;
        gsVirtual[0][88] = 200;
        gsVirtual[0][89] = 196;
        gsVirtual[0][90] = 184;
        gsVirtual[0][91] = 180;
        gsVirtual[0][92] = 176;
        gsVirtual[0][93] = 159;
        gsVirtual[0][94] = 150;
        gsVirtual[0][95] = 145;
        gsVirtual[0][96] = 140;
        gsVirtual[0][97] = 137;
        gsVirtual[0][98] = 133;
        gsVirtual[0][99] = 130;
        gsVirtual[0][100] = 125;
        gsVirtual[0][101] = 125;
        gsVirtual[0][102] = 125;
        gsVirtual[0][103] = 125;
        gsVirtual[0][104] = 125;
        gsVirtual[0][105] = 125;
        gsVirtual[0][106] = 125;
        gsVirtual[0][107] = 125;
        gsVirtual[0][108] = 125;
        gsVirtual[0][109] = 125;
        gsVirtual[0][110] = 125;
        gsVirtual[0][111] = 125;
        gsVirtual[0][112] = 125;
        gsVirtual[0][113] = 125;
        gsVirtual[0][114] = 125;
        gsVirtual[0][115] = 125;
        gsVirtual[0][116] = 125;
        gsVirtual[0][117] = 125;
        gsVirtual[0][117] = 130;
        gsVirtual[0][118] = 135;
        gsVirtual[0][119] = 142;
        gsVirtual[0][120] = 146;
        gsVirtual[0][121] = 154;
        gsVirtual[0][122] = 159;
        gsVirtual[0][123] = 168;
        gsVirtual[0][124] = 172;
        gsVirtual[0][125] = 174;
        gsVirtual[0][126] = 179;
        gsVirtual[0][127] = 185;
        gsVirtual[0][128] = 196;
        gsVirtual[0][129] = 200;
        gsVirtual[0][130] = 205;
        gsVirtual[0][131] = 210;
        gsVirtual[0][132] = 215;
        gsVirtual[0][133] = 220;
        gsVirtual[0][134] = 240;
        gsVirtual[0][135] = 260;
        gsVirtual[0][136] = 280;
        gsVirtual[0][137] = 300;
        gsVirtual[0][138] = 310;
        gsVirtual[0][139] = 330;
        gsVirtual[0][140] = 345;
        gsVirtual[0][141] = 360;
        gsVirtual[0][142] = 385;
        gsVirtual[0][143] = 400;
        gsVirtual[0][144] = 375;
        gsVirtual[0][145] = 360;
        gsVirtual[0][146] = 340;
        gsVirtual[0][147] = 320;
        gsVirtual[0][148] = 300;
        gsVirtual[0][149] = 280;
        gsVirtual[0][150] = 260;
        gsVirtual[0][151] = 240;
        gsVirtual[0][152] = 230;
        gsVirtual[0][153] = 220;
        gsVirtual[0][154] = 200;
        gsVirtual[0][155] = 180;
        gsVirtual[0][156] = 140;
        gsVirtual[0][157] = 130;
        gsVirtual[0][158] = 120;
        gsVirtual[0][159] = 110;
        gsVirtual[0][160] = 100;
        gsVirtual[0][161] = 90;
        gsVirtual[0][162] = 70;
        gsVirtual[0][163] = 90;
        gsVirtual[0][164] = 125;
        gsVirtual[0][165] = 155;
        gsVirtual[0][166] = 170;
        gsVirtual[0][167] = 190;
        gsVirtual[0][168] = 200;
        gsVirtual[0][169] = 230;
        gsVirtual[0][170] = 260;
        gsVirtual[0][171] = 290;
        gsVirtual[0][172] = 320;
        gsVirtual[0][173] = 350;
        gsVirtual[0][174] = 400;
        gsVirtual[0][175] = 420;
        gsVirtual[0][176] = 450;
        gsVirtual[0][177] = 470;
        gsVirtual[0][178] = 500;
        gsVirtual[0][179] = 480;
        gsVirtual[0][180] = 460;
        gsVirtual[0][181] = 430;
        gsVirtual[0][182] = 400;
        gsVirtual[0][183] = 380;
        gsVirtual[0][184] = 340;
        gsVirtual[0][185] = 380;
        gsVirtual[0][186] = 400;
        gsVirtual[0][187] = 420;
        gsVirtual[0][188] = 440;
        gsVirtual[0][189] = 450;
        gsVirtual[0][190] = 470;
        gsVirtual[0][191] = 490;
        gsVirtual[0][192] = 500;
        gsVirtual[0][193] = 530;
        gsVirtual[0][194] = 560;
        gsVirtual[0][195] = 600;
        gsVirtual[0][196] = 570;
        gsVirtual[0][197] = 540;
        gsVirtual[0][198] = 500;
        gsVirtual[0][199] = 400;
        gsVirtual[0][200] = 300;
        gsVirtual[0][201] = 200;
        gsVirtual[0][202] = 100;
        gsVirtual[0][203] = 50;
        gsVirtual[0][204] = 70;
        gsVirtual[0][205] = 80;
        gsVirtual[0][206] = 90;
        gsVirtual[0][207] = 100;
        gsVirtual[0][208] = 110;
        gsVirtual[0][209] = 120;
        gsVirtual[0][210] = 130;
        gsVirtual[0][211] = 140;
        gsVirtual[0][212] = 150;
        gsVirtual[0][213] = 160;
        gsVirtual[0][214] = 170;
        gsVirtual[0][215] = 180;
    }
}
