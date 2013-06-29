function info_input(ps_len){
	//choose Record or Feature
	var RecordFeature = createTest.radio[0].value;
	var FeatureStr = "";
	var CutoffStr = "";
	var pound_flag = 0;
	var FeaCheck = "";
	var FeaValue = "";
	var CutValue = "";
	if(createTest.radio[1].checked){
		RecordFeature = createTest.radio[1].value;
		//choose Features to compare
		for(var i=0;i<(ps_len);i++){
			FeaCheck = 'createTest.box'+i+'.checked';
			FeaValue = 'createTest.box'+i+'.value';
			CutValue = 'createTest.text'+i+'.value';
			if(eval(FeaCheck)){
				if(pound_flag == 1){
					FeatureStr += '#';
					CutoffStr += '#';
				}pound_flag = 1;
				FeatureStr += eval(FeaValue);
				if(eval(CutValue)==""){
					CutoffStr += '0';
				}else{
					CutoffStr += eval(CutValue);
				}
			}			
		}
	}
	var fromApp ="not connect";
	//send parameters to applet
	document.LTA.sendRorF(RecordFeature);
	document.LTA.sendFS(FeatureStr);
	document.LTA.sendCS(CutoffStr);
	//fromApp = document.LTA.get();
    //alert(RecordFeature);
}
function accessAppletResult()
{
	//alert(document.LTA.getOut());
    var Y_axis = document.LTA.getYaxis();
    var X_axis = document.LTA.getXaxis();
    var stepVal = document.LTA.getStep();
	DrawCurve(eval(X_axis), eval(Y_axis), eval(stepVal));
}