
$|=1;

while($next = <test.*>) {

  print "Reading $next\n";
  check($next);  
}

print "Reading test\n";
checkResidual();

sub checkResidual() {
  open(F, "test");
  
  while(<F>) {
    if($_ =~ /(.*) \d\d:/) {
      $p = $1;
    } else {
      die "In [test] unexpected line: [$_]\n";
    }
    
    if($old && $old != $p) {
      die "Mismatch in [test] unexpected line: [$_]\n";
    } else {
      print "  $_";
    }
    $old = $p;
  }
}

sub check() {
  my ($filename)  = @_;
  
  open(F, $filename);
  $_ = $filename;
  $p = s/test.//;
  while(<F>) {
    if($_ =~ /$p/) {
      print "  $_";
    } else {
      die "In [$filename] unexpected line: [$_]\n";
    }
  }
}
