

while(<STDIN>) {

  # Assuming TTCCLayout, check if the time format is correct
  #Example input:
  #20 Dec 1999 17:49:20.733 [main] ERROR DEB - Message 15  
  # or
  #20 dec. 1999 17:49:20,733 [main] ERROR DEB - Message 15  
  if(/\d\d (.{3,5})\.? \d{4} \d\d:\d\d:\d\d[,\.]\d{3} \[main\]/) {
    $month = $1;
    $i = index($_," [main]");
    print substr($_, $i);
  }
  # SimpleLayout
  elsif (/^(FATAL|ERROR|WARN|INFO|DEBUG) - Message/) {
    print $_;
  }
  elsif (/\(Min\.java:\d*\)/) {
    s/\(Min\.java:\d+\)$//;
    print $_;
  }
  elsif (/\(Compiled Code\)/) {
    s/\(Compiled Code\)$//;
    print $_;
  }
  elsif (/^java\.lang\.Exception: Just testing\./) {
    print $_;
  }
  else {
    wrongFormat($_);
  }

}

sub wrongFormat {
  my $line = $_;
  print "Wrong format.  Offending line: $_\n";
  exit 1; 
}


