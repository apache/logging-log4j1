
# Usage: perl filter.pl input output exceptionString layout 
# where exceptionString is the string to filter in stack traces
#       layout is one of NONE|LINE_NUMBER|RELATIVE|DATE|ABSOLUTE|ISO8601
#                                  

$INPUT=$ARGV[0];
$OUTPUT=$ARGV[1];
$EXSTR=$ARGV[2];
$LAYOUT=$ARGV[3];


open(IN, $INPUT) || die "Could not open $INPUT";
open(OUT, ">$OUTPUT") || die "Could not open $OUTPUT";

local($oldfh) = select(); select(OUT); $|=1; select($oldfh);

if ($LAYOUT =~ "NONE") {
  doNoneFilter();
}
elsif($LAYOUT =~ "LINE_NUMBER") {
  doLineNumberFilter();
}
else {
  doFilter($LAYOUT);
}

close(OUT);

sub wrongFormat {
  my $line = $_;
  print "Wrong format.  Offending line: \"$_\"\n";
  exit 1; 
}

sub doNoneFilter {  
  while(<IN>) {
    print OUT;
  }
}


sub doLineNumberFilter {  
  while(<IN>) {
    if(/\($EXSTR\.java:\d*\)/) {
      s/\($EXSTR\.java:\d+\)$/\($EXSTR\.java:XXX\)/;
      print OUT;
    }
    elsif(/\(Compiled Code\)/) {
      s/\(Compiled Code\)$/\($EXSTR\.java:XXX\)/;
      print OUT;
    }
    else {
      print OUT;
    }
  }
}


sub doFilter() {
  my $layout = $_[0];
  $basicPat = "\\[main\\] (FATAL|ERROR|WARN|INFO|DEBUG)";
  #$basicPat = 'main';  


  $timePat = "\\d\\d:\\d\\d:\\d\\d,\\d{3}";  # "17:49:20.733"
  
  #$datePat = "\\d\\d [A-Z][a-z]{2} \\d{4}"; # 20 Dec 1999
  # The month is locale dependent
  $datePat = "\\d\\d .{3,6} \\d{4}"; # 20 Dec 1999

  if($layout =~ "NULL") {
    $pattern = $basicPat;
  }
  # 98 [main] ERROR DEB - Message 15
  elsif($layout =~ "RELATIVE") {
    $pattern = "^\\d+ $basicPat";
  }
  # 17:49:20.733 [main] ERROR DEB - Message 15
  elsif($layout =~ "ABSOLUTE") {
     $pattern = "^$timePat $basicPat";
  }
  elsif ($layout =~ "DATE") {
    $pattern = "^$datePat $timePat $basicPat";
  }
  elsif ($layout =~ "ISO8601") {
    $pattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} $basicPat";
  }  
  else {
    die "Unknown layout [$layout]";
  }

  #print "PATTERN \"$pattern\"\n";
  while(<IN>) {
    if (/$pattern/o) {
      $i = index($_,"[main]");
      print OUT substr($_, $i);
    }
    elsif(/\($EXSTR\.java:\d*\)/) {
      s/\($EXSTR\.java:\d+\)$/\($EXSTR\.java:XXX\)/;
      print OUT;
    }
    elsif(/\(Compiled Code\)/) {
      s/\(Compiled Code\)$/\($EXSTR\.java:XXX\)/;
      print OUT;
    }
    elsif (/^java\.lang\.Exception: Just testing$/) {
      print OUT;
    }
    else {
      wrongFormat($_);
    }
  }
}
