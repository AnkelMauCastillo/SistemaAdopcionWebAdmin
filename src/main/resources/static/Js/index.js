function drawLineCharts(Role, Edad) {
    Highcharts.chart('container', {
        chart: {
            type: 'line',
            width: 500
        },
        title:{
            text: 'Line chart',
        },
        xAxis:{
            categories: Role
        },
        tooltip:{
            formatter: function () {
                return '<strong>' + this.x +': </strong>'+this.y;                
            }
        },
        series: [{
            data: Edad
        }]

    });
   
}

