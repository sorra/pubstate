export default {
  arrayRemoveValue(ary, value) {
    for (var i = 0; i < ary.length; i++) {
      if (ary[i] === value) {// Strict comparison
        ary.splice(i, 1)
        return i
      }
    }
  },

  limitStrLen(str, maxLen) {
    if (str.length > maxLen+3) {
      return str.substr(0, maxLen) + '...'
    } else {
      return str
    }
  },

  toErrorMsg(resp) {
    try {
      return (resp.responseText && JSON.parse(resp.responseText).errorMsg) || 'Network error'
    } catch (exception) {
      return 'Exception: ' + exception.message
    }
  },

  /**
   * pops an alert in the global fixed alerts-holder
   */
  popAlert(text, level, duration) {
    level = level || 'danger'
    duration = duration || 2000
    
    var $alert = $('<div class="alert-wrapper alert alert-' + level + '">').text(text)
    
    setTimeout(function () {
      $alert.fadeOut(1000).remove()
    }, duration)

    $('#alerts-holder').prepend($alert)
  }
}

$.fn.warnEmpty = function() {
  if (this.length == 0) {
    console.warn('Empty NodeList for '+this.selector+'!')
  }
  return this
}

$.fn.tipover = function (text, duration) {
  duration = duration || 1000

  var $node = this

  if ($node.length == 0) {
    console.error('tipover: selector ' + $node.selector + ' matches no element!')
    return
  }

  if (!$node.data('bs.tooltip')) {
      $node.tooltip({placement: 'top', trigger: 'manual'})
  }
  $node.data('bs.tooltip').options.title = text
  $node.tooltip('show')

  if (duration > 0) {
    window.setTimeout(function(){$node.tooltip('hide');}, duration)
  }
}
