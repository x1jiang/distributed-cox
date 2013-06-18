//$(function (X_axis,Y_axis) {
function DrawCurve(X_axis,Y_axis,stepVal) {
        $('#container').highcharts({
            chart: {
                type: 'line',
                marginRight: 130,
                marginBottom: 60
            },
            title: {
                text: 'Kaplan-Meier Curve for Testing Data',
                x: -20 //center
            },
            subtitle: {
                text: 'Survival function vs. Survival Time',
                x: -20
            },
            xAxis: {
                title: {
                    text: 'Survival Time'
                },
                labels:{
                	step: stepVal
                },
                categories: X_axis
            },
            yAxis: {
                title: {
                    text: 'Survival Function (%)'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            
            tooltip: {
                valueSuffix: '%'
            },
            
            plotOptions: {
                series: {
                    marker: {
                        radius: 0
                    }
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
            series: Y_axis
        });
    }
//});