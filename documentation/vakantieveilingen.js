javascript:
var secondsPlacingBidBeforeEnd = 1;
var secondsDifference = 1;
var bidDifferenceInEuro = 0;
var minOtherBid=10000;
function enhance() {
    insertMyBidElements();
    triggerBid();
    handleMyBidFromUrl();
}

function handleMyBidFromUrl() {
    var a = getURLParameter("mybid");
    if(a) {
        document.getElementById("maxBidInput").value = a;
    }

    var otherMinBid = getURLParameter("otherMinBid");
    if(otherMinBid){
        minOtherBid = otherMinBid;
    }
}
function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}
function insertMyBidElements() {
    var b = document.getElementById("jsAuctionBidContainer");
    var a = document.createElement("div");
    a.setAttribute("id", "myBidPanel");
    a.innerHTML = '<p>Max bid <input id="maxBidInput" type="text"/></p><span>Remaining seconds before bid <span id="remainingSecondsBeforeBid"/></span>';
    b.insertBefore(a, b.firstChild);
}
function getCurrentBid() {
    var a = document.getElementById("jsMainLotCurrentBid").innerText;
    return parseInt(a)
}
function triggerBid() {
    var a = getRemainingMillisecondsBeforeBidEnds();
    setTimeout(function () {
        submitBid()
    }, a);
    setInterval(function () {
        var b = getRemainingMillisecondsBeforeBidEnds() / 1000;
        if (b > 0) {
            document.getElementById("remainingSecondsBeforeBid").innerHTML = " = " + (b)
        }
    }, 1000)
}
function submitBid() {
    var a = document.getElementById("maxBidInput");
    var mybid = parseInt(a.value);
    var currentBid = parseInt(getCurrentBid());
    if (currentBid < mybid) {
        var d = currentBid + bidDifferenceInEuro;
        placeBid(d);
        alert("added bid = " + d)
    } else {
        setTimeout(function () {
            refreshAndBidAgain()
        }, 10*1000);
    }
}
function placeBid(a) {
    document.getElementById("jsActiveBidInput").value = a;
    var b = document.getElementById("jsActiveBidButton");
    b.click();
}
function refreshAndBidAgain() {
    var url = document.getElementById('jsBiddingContainer').getElementsByTagName('a')[0].href;
    url = url + "?mybid="+document.getElementById("maxBidInput").value;
    url = url + "&otherMinBid="+getCurrentBid();
    location.href = url;
}
function getRemainingMillisecondsBeforeBidEnds() {
    var d = document.getElementsByClassName("tsExpires")[0].textContent;
    var b = parseDate(d);
    var a = new Date();
    var c = b - a - (secondsDifference * 1000);
    return c - (secondsPlacingBidBeforeEnd * 1000)
}
function getProductId() {
    var a = document.getElementsByClassName("product")[0].id;
    return a.substring(3, 20)
}
function parseDate(f) {
    var a = new Date();
    var b = /(\d\d\d\d)(-)?(\d\d)(-)?(\d\d)(T)?(\d\d)(:)?(\d\d)(:)?(\d\d)(\.\d+)?(Z|([+-])(\d\d)(:)?(\d\d))/;
    if (f.toString().match(new RegExp(b))) {
        var e = f.match(new RegExp(b));
        var c = 0;
        a.setUTCDate(1);
        a.setUTCFullYear(parseInt(e[1], 10));
        a.setUTCMonth(parseInt(e[3], 10) - 1);
        a.setUTCDate(parseInt(e[5], 10));
        a.setUTCHours(parseInt(e[7], 10));
        a.setUTCMinutes(parseInt(e[9], 10));
        a.setUTCSeconds(parseInt(e[11], 10));
        if (e[12]) {
            a.setUTCMilliseconds(parseFloat(e[12]) * 1000)
        } else {
            a.setUTCMilliseconds(0)
        }
        if (e[13] != "Z") {
            c = (e[15] * 60) + parseInt(e[17], 10);
            c *= ((e[14] == "-") ? -1 : 1);
            a.setTime(a.getTime() - c * 60 * 1000)
        }
    } else {
        a.setTime(Date.parse(f))
    }
    return a
}
enhance();