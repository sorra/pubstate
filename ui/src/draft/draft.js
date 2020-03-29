import Common from '../common.js'

class DraftManager {

  constructor() {
    $('#drafts').delegate('.delete-btn', 'click', ev => {
      ev.preventDefault()

      let $btn = $(ev.target)
      const id = $btn.data('id')
      $.post('/drafts/' + id + '/delete')
        .done(resp => {
          Common.popAlert("Deleted the draft", 'info')
          $btn.parent('li').remove()
        })
        .fail(resp => {
          Common.popAlert('Faile to delete draft: ' + Common.toErrorMsg(resp), 'error')
        })
    })
  }
}

window.DraftManager = DraftManager
