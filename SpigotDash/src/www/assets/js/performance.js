var memchart;
var tpschart;
var cpuchart;
var worldchart;

var cpuoptions = {
    chart: {
        type: 'area',
        background: "transparent",
        zoom: {
            enabled: false
        },
        height: "100%"
    },
    series: [],
    dataLabels: {
        enabled: false
    },
    theme: {
        mode: 'dark'
    },
    title: {
        text: "CPU Usage (%)",
        align: "left"
    },
    noData: {
        text: "Loading..."
    }
}

var tpsoptions = {
    yaxis: {
        decimalsInFloat: 2
    },
    chart: {
        type: 'area',
        background: "transparent",
        zoom: {
            enabled: false
        },
        height: "100%"
    },
    series: [],
    dataLabels: {
        enabled: false
    },
    theme: {
        mode: 'dark'
    },
    title: {
        text: "TPS",
        align: "left"
    },
    noData: {
        text: "Loading..."
    }
}

var memoptions = {
    chart: {
        type: 'area',
        background: "transparent",
        zoom: {
            enabled: false
        },
        height: "100%"
    },
    series: [],
    dataLabels: {
        enabled: false
    },
    theme: {
        mode: 'dark'
    },
    title: {
        text: "Memory Usage (MB)",
        align: "left"
    },
    noData: {
        text: "Loading..."
    }
}

var worldoptions = {
    chart: {
        type: 'area',
        background: "transparent",
        zoom: {
            enabled: false
        },
        height: "100%"
    },
    series: [],
    dataLabels: {
        enabled: false
    },
    theme: {
        mode: 'dark'
    },
    title: {
        text: "Worlds",
        align: "left"
    },
    yaxis: [{
        show: false
    }, {
        show: false
    }, {
        show: false
    }],
    noData: {
        text: "Loading..."
    }
}

function initCharts() {
    tpschart = new ApexCharts(document.querySelector("#tpschart"), tpsoptions);
    tpschart.render();

    memchart = new ApexCharts(document.querySelector("#memorychart"), memoptions);
    memchart.render();

    cpuchart = new ApexCharts(document.querySelector("#cpuchart"), cpuoptions);
    cpuchart.render();

    worldchart = new ApexCharts(document.querySelector("#worldchart"), worldoptions);
    worldchart.render();
}

function transformDate(date) {
    return new Intl.DateTimeFormat('de-DE', { hour: '2-digit', minute: '2-digit' }).format(date);
}

async function updatePerformanceGraphs() {
    try {
        var data = await getJSONDataFromAPI("GET_PERFORMANCE_DATA");

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

        var WORLD_GRAPH = {
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

            WORLD_GRAPH.series[0].data.push({ x: elem_date, y: elem.WORLD_CHUNKS });
            WORLD_GRAPH.series[1].data.push({ x: elem_date, y: elem.WORLD_ENTITIES });
            WORLD_GRAPH.series[2].data.push({ x: elem_date, y: elem.WORLD_PLAYERS });

        });

        memchart.updateOptions(RAM_GRAPH.options);
        memchart.updateSeries(RAM_GRAPH.series);

        tpschart.updateSeries(TPS_GRAPH.series);

        cpuchart.updateSeries(CPU_GRAPH.series);

        worldchart.updateSeries(WORLD_GRAPH.series);
    } catch (err) {
        console.log(err);
    }
    setTimeout(updatePerformanceGraphs, 10000);
}

initCharts();
updatePerformanceGraphs();