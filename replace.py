#!/usr/bin/env python

import sys

if len(sys.argv) < 3:
  print 'Needs at least two arguments (input file and output file) ' + \
        'followed by replacement tuples.'

input_filename = sys.argv[1]
output_filename = sys.argv[2]

other_arguments = sys.argv[3:]
replacements = zip(other_arguments[0::2], other_arguments[1::2])

input_file = open(input_filename, 'r')
output_file = open(output_filename, 'w')

contents = input_file.read()
for to_replace, replace_with in replacements:
  contents = contents.replace(to_replace, replace_with)
output_file.write(contents)

input_file.close()
output_file.close()
