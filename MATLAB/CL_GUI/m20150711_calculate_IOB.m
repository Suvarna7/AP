%% Calculate insulin on board. Kamuran Turksoy
function IOB_total=m20150711_calculate_IOB(bolus_insulin,basal_insulin)
% this function is written based on 5 minutes sampling time. It needs to be
% modify for another sampling time
iob_bolus=[0.0494391409323673,0.0580373855536440,0.0757668499037829,0.0980848885912103,0.122686145196158,0.148897389631184,0.177141946974804,0.208473717778226,0.244180789845194,0.285458641484937,0.333152936238220,0.387571909076505,0.448368344074227,0.514491143554156,0.584206488705884,0.655188591677412,0.724680039139838,0.789721727325160,0.847452388537183,0.895477709135527,0.932309038992748,0.957871692424560,0.974082840593171,0.985498995383715];
iob_basal=[0.0494391409323673,0.0501181972866160,0.0513791680128310,0.0531577875112463,0.0553951328458261,0.0580373855536440,0.0610355979081826,0.0643454636364457,0.0679270930899165,0.0717447928694289,0.0757668499037829,0.0799653199823648,0.0843158207414618,0.0887973291045816,0.0933919831764966,0.0980848885912103,0.102863929313807,0.107719582896061,0.112644740185983,0.117634529491198,0.122686145196158,0.127798680833234,0.132972966607629,0.138211411376203,0.143517849080079,0.148897389631184,0.154356274252561,0.159901735272587,0.165541860373057,0.171285461291068,0.177141946974804,0.183121201193165,0.189233464599226,0.195489221247598,0.201899089565579,0.208473717778226,0.215223683787237,0.222159399503697,0.229291019634692,0.236628354923756,0.244180789845194,0.251957204752242,0.259965902479084,0.268214539396742,0.276710060922786,0.285458641484937,0.294465628938489,0.303735493437611,0.313271780760491,0.323077070088332,0.333152936238220,0.343499916349814,0.354117481025931,0.365004009926947,0.376156771819078,0.387571909076505,0.399244426637353,0.411168185413528,0.423335900154404,0.435739141764371,0.448368344074227,0.461212815066436,0.474260752554231,0.487499264314574,0.500914392674974,0.514491143554156,0.528213519956581,0.542064559920828,0.556026378921824,0.570080216726930,0.584206488705884,0.598384841594596,0.612594213712791,0.626812899635521,0.641018619318520,0.655188591677412,0.669299612620784,0.683328137537100,0.697250368235483,0.711042344340338,0.724680039139838,0.738139459888262,0.751396752562187,0.764428311070535,0.777210890918471,0.789721727325160,0.801938657795377,0.813840249144968,0.825405928980170,0.836616121630779,0.847452388537183,0.857897573091237,0.867935949930999,0.877553378689321,0.886737462196290,0.895477709135527,0.903765701154340,0.911595264427726,0.918962645676238,0.925866692637694,0.932309038992748,0.938294293744315,0.943830235050848,0.948928008513466,0.953602329916946,0.957871692424560,0.961758578226772,0.965289674643782,0.968496094681936,0.971413602043980,0.974082840593171,0.976549568271246,0.978864895470240,0.981085527858161,0.983274013658520,0.985498995383715,0.987835466022265,0.990365029679906,0.993176166674534,0.996364503085007];

if length(bolus_insulin)>=24
    IOB_bolus=iob_bolus*bolus_insulin(end-length(iob_bolus)+1:end);
else
    IOB_bolus=iob_bolus(end-length(bolus_insulin)+1:end)*bolus_insulin;
end

basal_insulin=reshape(repmat(basal_insulin,[5,1]),[5*length(basal_insulin),1])/240;

if length(basal_insulin)>=120
    IOB_basal=iob_basal*basal_insulin(end-length(iob_basal)+1:end);
else
    IOB_basal=iob_basal(end-length(basal_insulin)+1:end)*basal_insulin;
end

IOB_total=IOB_bolus+IOB_basal;