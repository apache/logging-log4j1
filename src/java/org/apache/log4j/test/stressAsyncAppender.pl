
# Copyright 2000, Ceki Gulcu. All rights reserved.

package SAA;

$SAA::size = 0;
$SAA::sizeAvg = 0;
$n = 0;
$expected = 0;
while(<STDIN>) {
  my @ndc;
  
  #print $_;
  $SAA::sizeAvg = ($SAA::sizeAvg*$n + $SAA::size)/(++$n);
  if($n % 10000 == 0) {
    print "n=$n, size=$SAA::size, sizeAverage: $SAA::sizeAvg,\n";
  }

  #25607 [Thread-17] DEBUG AsyncAppender - About to put new event in buffer.  
  if(/^\d+\s+\[(.+)\] DEBUG\s+(.*)\s-\s(.*)$/) {
    $thread = $1;
    $cat = $2;
    $msg = $3;
    
    #print "cat is [$cat]\n";

    if($cat =~ /AsyncAppender/) {
      if($msg =~ /About to put new event in buffer./) {
	#print "Got: About to put new event in buffer.\n";
	$SAA::size++;
      } elsif ($msg =~  /Notifying dispatcher to process events./) {
	if($SAA::size != 1) {
	  die "size not 1 ($SAA::size), as dispatcher is notified.";
	}
      } 
    } elsif ($cat =~ /Dispatcher/) {
      if($msg =~ /Waiting for new event to dispatch./) {
	if($SAA::size != 0) {
	  die "size not 0 ($SAA::size), as dispatcher waits for new events.";
	}
      } elsif($msg =~ /About to get new event./) {
	#print "--------------------Got: About to get new event.\n";	
	$SAA::size--;
	if($SAA::size < 0) {
	  die "Negative buffer size: $SAA::size\n";
	}
      }
    } elsif ($cat =~ root) {
      if($msg =~ /Message number (\d+)/) {
	$new = $1;
	if($expected != $new) {
	  die "expected $expected, but got $new\n";
	} else {
	  $expected = $new+1;
	}
      }
	  
    }
  }
}
