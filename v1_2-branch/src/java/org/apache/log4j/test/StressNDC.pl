
package StressDC;



$StressDC::threadCounter = 0;

while(<STDIN>) {
  my @ndc;
  
   #80 [3450f0] DEBUG  - Message number 43
  if(/^\d+\s+\[(.+)\] DEBUG\s+(.*)\s-\s(.*)$/) {
    $thread = $1;
    $dc = $2;
    $msg = $3;

    if($msg =~ /In run loop/) {
      @ndc = split(/\s/, $dc);
      $StressDC::NDC{$thread} = [ @ndc ];
      #print "Inherited [$thread] - @ndc. \n";
    }

    if(!checkNDC($thread, $dc)) {
      print "Offending line: $_";
      exit 1;
    }
    
    if($msg =~ /New StressNDC, threadCounter = (\d+)/) {
      if($1 != ++$StressDC::threadCounter) {
	print "Number of reported threads ($1) differs from expected " .
	      "number $StressDC::threadCounter.\n";
	print "Offending line: $_";
	exit 1;
      }
    }
    elsif($msg =~ /Removing NDC for this thread./) {
      delete $StressDC::NDC{$thread};
    }  
    elsif($msg =~ /Exiting run loop. (\d+)/) {      
      if($1 != --$StressDC::threadCounter) {
	print "Number of reported threads ($1) differs from expected " .
	      "number ($StressDC::threadCounter).\n";
	print "Offending line: $_";
      }
    }
    elsif($msg =~ /push\((.*)\)/) {
      @ndc = @{$StressDC::NDC{$thread}} ;
      push(@ndc, $1);
      $StressDC::NDC{$thread} = [ @ndc ];
     # print "pushed $1 to [$thread] - @{ndc}\n";
    }
    elsif($msg =~ /pop\(\)/) {
      @ndc = @{$StressDC::NDC{$thread}};
      pop(@ndc);
      $StressDC::NDC{$thread} = [ @ndc ];
      #print "poped [$thread] - @ndc\n";            
    }
  } elsif (/Lazy NDC removal for thread \[(.+)\] \(/) {
    delete $StressDC::NDC{$1};
  } else {
    print "Non matching line: $_";
  }
}

sub checkNDC() {
  my ($thread, $ndc) = @_;

  @expected = @{$StressDC::NDC{$thread}};

  if($ndc != "@expected") {
    print "Output NDC ($ndc) is different from expected NDC (@expected).\n";
    return 0;	
  }
  else {
    return 1;
  }
}
