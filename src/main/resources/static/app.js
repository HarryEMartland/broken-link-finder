$(function(){

    var socket = new SockJS('http://'+window.location.host+window.location.pathname+'hello');
    var stompClient = Stomp.over(socket);
    var urlScans = {};
    var urlResults = {};

    stompClient.connect({}, function(frame) {

					$('#errorWrapper').on('click','button.close', function(e){
						$(e.target).parent().remove();
					})

                    stompClient.subscribe('/user/topic/scanResult', function(greeting){
                        var scanResult = JSON.parse(greeting.body);
                        if(scanResult.throwable){
                            showError(scanResult.throwable)
                        }else{
                            urlScans[scanResult.parentUrl] = scanResult.childUrls;
                            setScanResults(scanResult.childUrls);
                        }
                    });

                    function showError(error){
                        $('.hideUntilSearch').hide();
                        $('.showLoading').hide();
                        $('#errorWrapper').append('<div class="alert alert-danger alert-dismissible" role="alert">'+
                                                     '<button type="button" class="close" data-dismiss="alert" aria-label="Close">'+
                                                     '<span aria-hidden="true">&times;</span></button>'+
                                                     '<strong>Error!</strong> '+error.message+
                                                   '</div>')
                    }

                    function setScanResults(urls){
                        $('.hideUntilSearch').show();
                        $('.hideLoading').show();
                        $('.showLoading').hide();
                        updatePercents();
                        var scanUrlResults = $('#scanUrlResults');
                        scanUrlResults.empty();

                        $(urls).each(function(i, url){
                            scanUrlResults.append('<div class="scanUrlResult col-lg-12" data-url="'+url+'">'+
                            '<a href="#" title="Processing..." data-url="'+encodeURIComponent(url)+'" class="'+getUrlStatus(urlResults[url])+'">'+url+'</a>'+
                            '</div>');
                        });
                    }

                    function getUrlStatus(isValid){
                        if(isValid == undefined){
                            return "scanUrlAnchor";
                        }else if(isValid){
                        return 'scanUrlAnchor text-success';
                        }else{
                        return 'scanUrlAnchor text-danger';
                        }
                    }

                    stompClient.subscribe('/user/topic/urlResult', function(greeting){
                        var urlResult = JSON.parse(greeting.body);
                        urlResults[urlResult.url] = urlResult.valid;
                        updatePercents();
                        $('.scanUrlAnchor[data-url=\''+encodeURIComponent(urlResult.url)+'\']')
                        .attr('class',getUrlStatus(urlResult.valid))
                        .attr('title', getTitle(urlResult));
                    });

					function getTitle(urlResult){
						if(urlResult.isValid || urlResult.statusCode == 200){
							return "OK";
						}if(urlResult.statusCode > 0){
							return "Status: " + urlResult.statusCode;
						}else{
							return "Error: " + urlResult.throwable.message;
						}
					}

					function updatePercents(){
						var scan = urlScans[$('#scanUrlTxt').val()]
						var inProgress = 0.;
						var successful = 0.;
						var broken = 0.;
						var total = 0.;
						$(scan).each(function(i, url){
							result = urlResults[url];
							total++;
							if(result === true){
								successful++;
							}else if (result === false){
								broken++;
							}else{
								inProgress++;
							}
						})
						$('#processingPer').width(inProgress/total*100 +"%").html('Processing ' + inProgress);
						$('#successPer').width(successful/total*100 +"%").html('Success ' + successful);
						$('#brokenPer').width(broken/total*100 +"%").html('Broken ' + broken);
					}

					$('#scanUrlResults').on('click','.scanUrlAnchor', function(e){
						var url = $(e.target).parent().attr('data-url');
						$('#scanUrlTxt').val(url)
						doScan(url);
					});

                    $('#scanUrlForm').on('submit', function(){
                        doScan();
                    })

                    function doScan(){
                        $('.hideLoading').hide();
                        $('.showLoading').show();
                        var url = $('#scanUrlTxt').val();
						if(urlScans[url]){
                            setScanResults(urlScans[url]);
                        }else{
                            stompClient.send("/app/scan", {}, JSON.stringify({ 'url': url }));
                        }
                    }

                });

});