angular.module('app.chart.directives', []).
directive('morrisInternalChart', 
    function() {
      return {
        restrict: 'A',
        scope: {
          data: '='
        },
        link: function(scope, ele, attrs) {
          var colors, data, func, options;
          scope.$watch('data',function(){
              console.log('multi',scope.data);
              data = scope.data;
              switch (attrs.type) {
              case 'bar':
                  if (attrs.barColors === void 0 || attrs.barColors === '') {
                    colors = null;
                  } else {
                    colors = JSON.parse(attrs.barColors);
                  }
                  var b = ele[0];
                	while (b.hasChildNodes())
                	  b.removeChild(b.lastChild);
                  options = {
                    element: ele[0],
                    data: data,
                    xkey: attrs.xkey,
                    ykeys: JSON.parse(attrs.ykeys),
                    labels: JSON.parse(attrs.labels),
                    barColors: colors || ['#0b62a4', '#7a92a3', '#4da74d', '#afd8f8', '#edc240', '#cb4b4b', '#9440ed'],
                    stacked: attrs.stacked || null,
                    resize: true
                  };
                  var a = new Morris.Bar(options);
                  return a;
              case 'line':
                  if (attrs.lineColors === void 0 || attrs.lineColors === '') {
                    colors = null;
                  } else {
                    colors = JSON.parse(attrs.lineColors);
                  }
                  var b = ele[0];
                	while (b.hasChildNodes())
                	  b.removeChild(b.lastChild);
                  options = {
                    element: ele[0],
                    data: data,
                    xkey: attrs.xkey,
                    ykeys: JSON.parse(attrs.ykeys),
                    labels: JSON.parse(attrs.labels),
                    lineColors: colors || ['#0b62a4', '#7a92a3', '#4da74d', '#afd8f8', '#edc240', '#cb4b4b', '#9440ed'],
                    stacked: attrs.stacked || null,
                    resize: true
                  };
                  var a = new Morris.Line(options);
                  return a;
              case 'donut':
              if (attrs.colors === void 0 || attrs.colors === '') {
                  colors = null;
                } else {
                  colors = JSON.parse(attrs.colors);
                }
              	var b = ele[0];
              	while (b.hasChildNodes())
              	  b.removeChild(b.lastChild);
                options = {
                  element: b,
                  data: data,
                  colors: colors || ['#0B62A4', '#3980B5', '#679DC6', '#95BBD7', '#B0CCE1', '#095791', '#095085', '#083E67', '#052C48', '#042135'],
                  resize: true
                };
                if (attrs.formatter) {
                  func = new Function('y', 'data', attrs.formatter);
                  options.formatter = func;
                }
                var a = new Morris.Donut(options);
                return a;
              }
            },true);
          data = scope.data;
          switch (attrs.type) {
          case 'bar':
              if (attrs.barColors === void 0 || attrs.barColors === '') {
                colors = null;
              } else {
                colors = JSON.parse(attrs.barColors);
              }
              options = {
                element: ele[0],
                data: data,
                xkey: attrs.xkey,
                ykeys: JSON.parse(attrs.ykeys),
                labels: JSON.parse(attrs.labels),
                barColors: colors || ['#0b62a4', '#7a92a3', '#4da74d', '#afd8f8', '#edc240', '#cb4b4b', '#9440ed'],
                stacked: attrs.stacked || null,
                resize: true
              };
              //return new Morris.Bar(options);
          	case 'line':
              if (attrs.lineColors === void 0 || attrs.lineColors === '') {
                colors = null;
              } else {
                colors = JSON.parse(attrs.lineColors);
              }
              options = {
                element: ele[0],
                data: data,
                xkey: attrs.xkey,
                ykeys: JSON.parse(attrs.ykeys),
                labels: JSON.parse(attrs.labels),
                lineColors: colors || ['#0b62a4', '#7a92a3', '#4da74d', '#afd8f8', '#edc240', '#cb4b4b', '#9440ed'],
                stacked: attrs.stacked || null,
                resize: true
              };
            case 'donut':
              if (attrs.colors === void 0 || attrs.colors === '') {
                colors = null;
              } else {
                colors = JSON.parse(attrs.colors);
              }
              options = {
                element: ele[0],
                data: data,
                colors: colors || ['#0B62A4', '#3980B5', '#679DC6', '#95BBD7', '#B0CCE1', '#095791', '#095085', '#083E67', '#052C48', '#042135'],
                resize: true
              };
              if (attrs.formatter) {
                func = new Function('y', 'data', attrs.formatter);
                options.formatter = func;
              }
              //return new Morris.Donut(options);
          }
        }
      };
    }
  );