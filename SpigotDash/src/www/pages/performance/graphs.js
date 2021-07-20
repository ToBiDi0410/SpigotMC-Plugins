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
            background: "transparent"
        },
        theme: {
            mode: theme
        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: 'CPU Usage',
            align: 'left'
        },
        yaxis: {
            min: 0
        }
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
            background: "transparent"
        },
        theme: {
            mode: theme
        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: 'RAM Usage',
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
            background: "transparent"
        },
        theme: {
            mode: theme,

        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: 'TPS History',
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
            background: "transparent"
        },
        theme: {
            mode: theme,

        },
        dataLabels: {
            enabled: false
        },
        title: {
            text: 'Engine Stats',
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
                name: "Allocated",
                data: []
            }, {
                name: "Used",
                data: []
            }]
        }

        var TPS_GRAPH = {
            series: [{
                name: "TPS (Ticks per Second)",
                data: []
            }]
        }

        var CPU_GRAPH = {
            series: [{
                name: "Host CPU Load",
                data: []
            }, {
                name: "Load caused by Server",
                data: []
            }]
        }

        var ENGINE_GRAPH = {
            series: [{
                    name: "Chunks",
                    data: []
                }, {
                    name: "Entities",
                    data: []
                },
                {
                    name: "Players",
                    data: []
                }
            ]
        }

        data.forEach((elem) => {
            var elem_date = transformDate(new Date(elem.DATETIME));
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
    return new Intl.DateTimeFormat('de-DE', { hour: '2-digit', minute: '2-digit' }).format(date);
}