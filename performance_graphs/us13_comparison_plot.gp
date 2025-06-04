set terminal png size 1000,600
set output 'us13_comparison_performance.png'
set title 'US13 Performance Comparison - All Train Types'
set xlabel 'Number of Vertices'
set ylabel 'Execution Time (ms)'
set grid
set key outside
plot 'us13_comparison_data.txt' using 1:2 with linespoints title 'Steam', \
     'us13_comparison_data.txt' using 1:3 with linespoints title 'Diesel', \
     'us13_comparison_data.txt' using 1:4 with linespoints title 'Electric'
