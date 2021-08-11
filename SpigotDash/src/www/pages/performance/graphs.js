var tpschart;
var ramchart;
var enginechart;
var cpuchart;

function initCPUChart() {
    var options = {
        series: [],
        chart: {
            type: 'area',
            id: "chart1",
            background: "transparent",
            height: "100%"
        },
        theme: {
            mode: theme
        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: '%T%CPU_USAGE%T%',
            align: 'left'
        },
        yaxis: {
            min: 0
        },
    };

    cpuchart = new ApexCharts(document.querySelector("#cpuchart"), options);
    cpuchart.render();
}

function initRAMChart() {
    var options = {
        series: [],
        chart: {
            type: 'area',
            id: "chart2",
            background: "transparent",
            height: "100%"
        },
        theme: {
            mode: theme
        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: '%T%RAM_USAGE%T%',
            align: 'left'
        },
        yaxis: {
            min: 0
        }
    };

    ramchart = new ApexCharts(document.querySelector("#ramchart"), options);
    ramchart.render();
}

function initTPSChart() {
    var options = {
        series: [],
        chart: {
            type: 'area',
            id: "chart3",
            background: "transparent",
            height: "100%"
        },
        theme: {
            mode: theme,

        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: '%T%TPS_HISTORY%T%',
            align: 'left'
        },
        yaxis: {
            min: 0
        }
    };

    tpschart = new ApexCharts(document.querySelector("#tpschart"), options);
    tpschart.render();
}

function initEngineChart() {
    var options = {
        series: [],
        chart: {
            type: 'area',
            id: "chart4",
            background: "transparent",
            height: "100%"
        },
        theme: {
            mode: theme,

        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: '%T%ENGINE_STATS%T%',
            align: 'left'
        },
        yaxis: [{
            show: false
        }, {
            show: false,
        }, {
            show: false
        }]
    };

    enginechart = new ApexCharts(document.querySelector("#enginechart"), options);
    enginechart.render();
}



async function initPage() {
    initCPUChart();
    initRAMChart();
    initTPSChart();
    initEngineChart();
    fixDuplicates();
    curr_task = updateData;
}

function fixDuplicates() {
    var ids = [];
    document.querySelectorAll(".apexcharts-canvas").forEach((elem) => {
        var chartid = elem.id.replace("apexcharts", "");
        if (ids.includes(chartid)) {
            elem.remove();
        } else {
            ids.push(chartid);
        }
    });
}

async function updateData() {
    try {

        var data = await getDataFromAPI({ method: "GET_GRAPHS" });

        var RAM_GRAPH = {
            options: {
                yaxis: {
                    min: 0,
                    max: data[0].MEMORY_MAX
                }
            },
            series: [{
                name: "%T%ALLOCATED%T%",
                data: []
            }, {
                name: "%T%USED%T%",
                data: []
            }]
        }

        var TPS_GRAPH = {
            series: [{
                name: "%T%TPS_LONG%T%",
                data: []
            }]
        }

        var CPU_GRAPH = {
            series: [{
                name: "%T%CPU_LOAD_HOST%T%",
                data: []
            }, {
                name: "%T%CPU_LOAD_SERVER%T%",
                data: []
            }]
        }

        var ENGINE_GRAPH = {
            series: [{
                    name: "%T%CHUNKS%T%",
                    data: []
                }, {
                    name: "%T%ENTITIES%T%",
                    data: []
                },
                {
                    name: "%T%PLAYERS%T%",
                    data: []
                }
            ]
        }

        data.forEach((elem) => {
            var elem_date = transformDate(new Date(elem.DATETIME + " UTC"));
            RAM_GRAPH.series[0].data.push({ x: elem_date, y: elem.MEMORY_ALLOCATED });
            RAM_GRAPH.series[1].data.push({ x: elem_date, y: elem.MEMORY_USED });

            TPS_GRAPH.series[0].data.push({ x: elem_date, y: parseFloat(elem.TPS).toFixed(2) });

            CPU_GRAPH.series[0].data.push({ x: elem_date, y: elem.CPU_LOAD_SYSTEM });
            CPU_GRAPH.series[1].data.push({ x: elem_date, y: elem.CPU_LOAD_PROCESS });

            ENGINE_GRAPH.series[0].data.push({ x: elem_date, y: elem.WORLD_CHUNKS });
            ENGINE_GRAPH.series[1].data.push({ x: elem_date, y: elem.WORLD_ENTITIES });
            ENGINE_GRAPH.series[2].data.push({ x: elem_date, y: elem.WORLD_PLAYERS });

        });

        ramchart.updateOptions(RAM_GRAPH.options);
        ramchart.updateSeries(RAM_GRAPH.series);

        tpschart.updateSeries(TPS_GRAPH.series);

        cpuchart.updateSeries(CPU_GRAPH.series);

        enginechart.updateSeries(ENGINE_GRAPH.series);
    } catch (err) {}

}

function transformDate(date) {
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}