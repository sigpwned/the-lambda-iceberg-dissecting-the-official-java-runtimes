import sys, re, csv

pattern = sys.argv[1]

PATTERN = re.compile(pattern)

output = csv.writer(sys.stdout)

for runtime in [ "8", "11", "17" ]:
    for memory in [ "128", "256", "512", "1024", "2048", "4096", "8192", "10240" ]:
        with open(f"{runtime}/{memory}.txt", "r") as file:
            for line in file:
                datum = None

                m = PATTERN.search(line)
                if m is not None:
                    datum = m[1]

                if datum is not None:
                    output.writerow([ runtime+"/"+memory, runtime, memory, datum ])
